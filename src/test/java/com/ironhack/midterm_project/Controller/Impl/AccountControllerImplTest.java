package com.ironhack.midterm_project.Controller.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm_project.Controller.Interface.Enum.Status;
import com.ironhack.midterm_project.Model.Accounts.CheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import com.ironhack.midterm_project.Model.Accounts.Transaction;
import com.ironhack.midterm_project.Model.Embedded.Address;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Model.Users.Role;
import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;
import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.*;
import com.ironhack.midterm_project.Service.Impl.CheckingAccountServiceImpl;
import com.ironhack.midterm_project.Service.Impl.CreditCardAccountServiceImpl;
import com.ironhack.midterm_project.Service.Impl.SavingsAccountServiceImpl;
import com.ironhack.midterm_project.Service.Impl.ThirdPartyUserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SavingsAccountServiceImpl savingsAccountService;

    @Autowired
    private ThirdPartyUserServiceImpl thirdPartyUserService;

    @Autowired
    private CheckingAccountServiceImpl checkingAccountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreditCardAccountServiceImpl creditCardAccountService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User adminUser;
    private AccountHolder accountHolder;
    private AccountHolder accountHolderSecundary;
    private AccountHolder recipientAccountHolder;
    private ThirdPartyUser thirdPartyUser;

    private Role adminRole;
    private Role userRole;
    private Role userRole2;

    private Address address1 = new Address("Menorca street","Madrid","Spain","28009");

    private SavingsAccount savingsAccount1;
    private SavingsAccount recipientSavingAccount;

    private CreditCardAccount creditCardAccount1;
    private CheckingAccount checkingAccount1;

    private Currency eur = Currency.getInstance("EUR");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        adminRole = new Role("ADMIN");
        userRole = new Role("USER");
        userRole2 = new Role("USER");

        adminUser = new User("admin",passwordEncoder.encode("123456"),"Sofia de la Fuente");
        adminRole.setUser(adminUser);

        accountHolder = new AccountHolder("sofidelaf",passwordEncoder.encode("123456"),"Sofia de la Fuente", LocalDate.of(1998,11,1),address1);
        userRole.setUser(accountHolder);


        recipientAccountHolder = new AccountHolder("juandelaf",passwordEncoder.encode("123456"),"Juan de la Fuente", LocalDate.of(1971,5,25),address1);
        userRole2.setUser(recipientAccountHolder);

        accountHolderSecundary = new AccountHolder("secondary",passwordEncoder.encode("123456"),"Francisco de la Fuente", LocalDate.of(2011,12,14),address1);

        thirdPartyUser = thirdPartyUserService.create("third_party","123456","Alejandro de la Fuente","1234");

        userRepository.saveAll(List.of(adminUser));
        accountHolderRepository.saveAll(List.of(accountHolder,accountHolderSecundary,recipientAccountHolder));
        roleRepository.saveAll(List.of(adminRole,userRole,userRole2));

        BigDecimal initialBalance = new BigDecimal(10000);
        savingsAccount1 = savingsAccountService.createSavingsAccount(accountHolder.getId(),null, new Money(initialBalance,eur),"HOLA",null,null);
        recipientSavingAccount = savingsAccountService.createSavingsAccount(recipientAccountHolder.getId(),null, new Money(initialBalance,eur),"HOLA",null,null);

        creditCardAccount1 = creditCardAccountService.createCreditCardAccount(accountHolder.getId(),null,"HOLA",null,null);
        checkingAccount1 = checkingAccountService.createCheckingAccount(recipientAccountHolder.getId(), accountHolder.getId(), new Money(initialBalance,eur),"HOLA");

    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAll_invalidAccess() throws Exception{

        // Without authentication
        MvcResult mvcResult= mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Without user authentication
        MvcResult mvcResult2= mockMvc.perform(get("/accounts")
                        .with(httpBasic("secondary","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    void updateBalance_validAccess_badRequest() throws Exception{
        BigDecimal newBalance = new BigDecimal(2000);


        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/update_balance/"+ (long)1000)
                        .param("balance",newBalance.toString())
                        .with(httpBasic("admin","123456"))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void updateBalance_invalidAccess() throws Exception{
        BigDecimal newBalance = new BigDecimal(2000);

        //Without authentication

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/update_balance/"+ creditCardAccount1.getId())
                        .param("balance",newBalance.toString())
                )
                .andExpect(status().isUnauthorized())
                .andReturn();


        //Without user authentication


        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/update_balance/"+ creditCardAccount1.getId())
                        .param("balance",newBalance.toString())
                        .with(httpBasic("secondary","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    void getAll_validAccess_isOK() throws Exception{

        MvcResult mvcResult2= mockMvc.perform(get("/accounts")
                        .with(httpBasic("admin","123456"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

    }

    @Test
    void updateBalance_validAccess_noContent() throws Exception{
        BigDecimal newBalance = new BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN);


        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/update_balance/"+ creditCardAccount1.getId())
                        .param("balance",newBalance.toString())
                        .with(httpBasic("admin","123456"))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(newBalance,accountRepository.findById(creditCardAccount1.getId()).get().getBalance().getAmount());


        MvcResult mvcResult3= mockMvc.perform(patch("/accounts/update_balance/"+ savingsAccount1.getId())
                        .param("balance",newBalance.toString())
                        .with(httpBasic("admin","123456"))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(newBalance,accountRepository.findById(savingsAccount1.getId()).get().getBalance().getAmount());

    }

    @Test
    void updateOwnBalance_invalidAccess() throws Exception {
        //Without authentication

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/my_account/update_balance/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //authentication failed

        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/my_account/update_balance/1")
                        .with(httpBasic("sofia de la fuente","1234"))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

    }

    @Test
    void updateOwnBalance_validAccessRequest_BadRequest() throws Exception {

        //Invalid Id

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/my_account/update_balance/1000")
                        .with(httpBasic("sofidelaf","123456"))
                        .param("action_type","ingress")
                        .param("amount","1000.0565")
                )
                .andExpect(status().isBadRequest())
                .andReturn();


        // Non existing account holder

        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/my_account/update_balance/"+savingsAccount1.getId())
                        .with(httpBasic("admin","123456"))  //is only a User Admin, non an account holder
                        .param("action_type","ingress")
                        .param("amount","1000.0565")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        //non valid amount
        //negative value

        MvcResult mvcResult3= mockMvc.perform(patch("/accounts/my_account/update_balance/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))  //is only a User Admin, non an account holder
                        .param("action_type","ingress")
                        .param("amount","-1000.0565")
                )
                .andExpect(status().isBadRequest())
                .andReturn();


        //incorrect action

        MvcResult mvcResult5= mockMvc.perform(patch("/accounts/my_account/update_balance/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))  //is only a User Admin, non an account holder
                        .param("action_type","ingres")
                        .param("amount","1000.056")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        MvcResult mvcResult6= mockMvc.perform(patch("/accounts/my_account/update_balance/"+savingsAccount1.getId())
                        .with(httpBasic("daviddiaz","123456"))
                        .param("action_type","extra")
                        .param("amount","1000.05")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void transfer_invalidAccess() throws Exception {
        //Without authentication

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/my_account/transfer/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //authentication failed

        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/my_account/transfer/1")
                        .with(httpBasic("sofia de la fuente","1234"))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

    }

    @Test
    void transfer_validAccessRequest_BadRequest() throws Exception {

        //Invalid Id

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/my_account/transfer/1000")
                        .with(httpBasic("sofidelaf","123456"))
                        .param("amount","100")
                        .param("recipient_name","Juan de la Fuente")
                        .param("recipient_account_id",recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Invalid Recipient Id

        MvcResult mvcResult10= mockMvc.perform(patch("/accounts/my_account/transfer/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))
                        .param("amount","100")
                        .param("recipient_name","Juan de la Fuente")
                        .param("recipient_account_id","1000")
                )
                .andExpect(status().isBadRequest())
                .andReturn();


        // Non existing account holder

        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/my_account/transfer/"+savingsAccount1.getId())
                        .with(httpBasic("admin","123456"))  //is only a User Admin, non an account holder
                        .param("amount","100")
                        .param("recipient_name","Juan de la Fuente")
                        .param("recipient_account_id",recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        //non valid amount
        //negative value

        MvcResult mvcResult3= mockMvc.perform(patch("/accounts/my_account/transfer/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))  //is only a User Admin, non an account holder
                        .param("amount","-100")
                        .param("recipient_name","Juan de la Fuente")
                        .param("recipient_account_id",recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isBadRequest())
                .andReturn();


        //incorrect name

        MvcResult mvcResult5= mockMvc.perform(patch("/accounts/my_account/transfer/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))  //is only a User Admin, non an account holder
                        .param("amount","100")
                        .param("recipient_name","Juan de la Fuante")
                        .param("recipient_account_id",recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        MvcResult mvcResult6= mockMvc.perform(patch("/accounts/my_account/transfer/"+savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456"))
                        .param("amount","100")
                        .param("recipient_name","JuandelaFuente")
                        .param("recipient_account_id",recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void transfer_validRequest_isNoContent() throws Exception {
        //amount greater than actual balance
        BigDecimal amount = new BigDecimal(10.55);
        BigDecimal inicialOwnAccountBalance = savingsAccount1.getBalance().getAmount();
        BigDecimal inicialRecipientAccountBalance = recipientSavingAccount.getBalance().getAmount();

        MvcResult mvcResult1 = mockMvc.perform(patch("/accounts/my_account/transfer/" + savingsAccount1.getId())
                        .with(httpBasic("sofidelaf","123456" ))
                        .param("amount", amount.toString())
                        .param("recipient_name", "Juan de la Fuente")
                        .param("recipient_account_id", recipientSavingAccount.getId().toString())
                )
                .andExpect(status().isNoContent())
                .andReturn();

        //own account
        assertEquals(inicialOwnAccountBalance.subtract(amount).setScale(2,RoundingMode.HALF_EVEN),accountRepository.findById(savingsAccount1.getId()).get().getBalance().getAmount());
        //recipient account
        assertEquals(inicialOwnAccountBalance.add(amount).setScale(2,RoundingMode.HALF_EVEN),accountRepository.findById(recipientSavingAccount.getId()).get().getBalance().getAmount());
    }

    @Test
    void thirdPartyTransfer_invalidAccess() throws Exception{
        BigDecimal transfer = new BigDecimal(10);
        //Without authentication

        MvcResult mvcResult= mockMvc.perform(patch("/accounts/third_party_transfer/"+ thirdPartyUser.getHashedKey())
                        .param("action_type","send")
                        .param("amount","10.00")
                        .param("recipient_account_id",savingsAccount1.getId().toString())
                        .param("secret_key",savingsAccount1.getSecretKey())
                )
                .andExpect(status().isUnauthorized())
                .andReturn();


        // Without user authentication

        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/third_party_transfer/"+ thirdPartyUser.getHashedKey())
                        .param("action_type","send")
                        .param("amount","10.00")
                        .param("recipient_account_id",savingsAccount1.getId().toString())
                        .param("secret_key",savingsAccount1.getSecretKey())
                        .with(httpBasic("secondary","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();

        // With incorrect hashedKey

        MvcResult mvcResult3= mockMvc.perform(patch("/accounts/third_party_transfer/"+"hola")
                        .param("action_type","send")
                        .param("amount","10.00")
                        .param("recipient_account_id",savingsAccount1.getId().toString())
                        .param("secret_key",savingsAccount1.getSecretKey())
                        .with(httpBasic("third_party","123456"))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void thirdPartyTransfer_validAccess_isNoContent() throws Exception{
        BigDecimal amount = new BigDecimal(10);

        MvcResult mvcResult1= mockMvc.perform(patch("/accounts/third_party_transfer/"+thirdPartyUser.getHashedKey())
                        .param("action_type","ReCeive ")
                        .param("amount","10")
                        .param("recipient_account_id",savingsAccount1.getId().toString())
                        .param("secret_key",savingsAccount1.getSecretKey())
                        .with(httpBasic("third_party","123456"))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        BigDecimal finalValue1 = savingsAccount1.getBalance().getAmount().subtract(amount).setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(finalValue1,accountRepository.findById(savingsAccount1.getId()).get().getBalance().getAmount());


        MvcResult mvcResult2= mockMvc.perform(patch("/accounts/third_party_transfer/"+thirdPartyUser.getHashedKey())
                        .param("action_type"," Send")
                        .param("amount","10")
                        .param("recipient_account_id",creditCardAccount1.getId().toString())
                        .param("secret_key",creditCardAccount1.getSecretKey())
                        .with(httpBasic("third_party","123456"))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        BigDecimal finalValue2 = creditCardAccount1.getBalance().getAmount().add(amount).setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(finalValue2,accountRepository.findById(creditCardAccount1.getId()).get().getBalance().getAmount());
    }

    @Test
    void thirdPartyTransfer_accountFrozen_isForbidden() throws Exception{
        savingsAccount1.setStatus(Status.FROZEN);
        accountRepository.save(savingsAccount1);
        BigDecimal amount = new BigDecimal(10);

        MvcResult mvcResult1= mockMvc.perform(patch("/accounts/third_party_transfer/"+thirdPartyUser.getHashedKey())
                        .param("action_type","ReCeive ")
                        .param("amount","10")
                        .param("recipient_account_id",savingsAccount1.getId().toString())
                        .param("secret_key",savingsAccount1.getSecretKey())
                        .with(httpBasic("third_party","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    void getPersonalAccountInformation_invalidPublicAccess_UnAuthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/accounts/my_accounts")
                )
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getPersonalAccountInformation_validUserAccessByPrimaryOwner_AccountsNoUpdated() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/my_accounts")
                        .with(httpBasic("sofidelaf","123456"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //validation of showing checking account
        assertTrue(mvcResult.getResponse().getContentAsString().contains(checkingAccount1.getId().toString()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(checkingAccount1.getSecretKey()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(checkingAccount1.getPrimaryOwner().getName()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(checkingAccount1.getMinimumBalance().getAmount().toString()));

        //validation of showing credit card account
        assertTrue(mvcResult.getResponse().getContentAsString().contains(creditCardAccount1.getId().toString()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(creditCardAccount1.getSecretKey()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(creditCardAccount1.getPrimaryOwner().getName()));

        //validation of showing saving account
        assertTrue(mvcResult.getResponse().getContentAsString().contains(savingsAccount1.getId().toString()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(savingsAccount1.getSecretKey()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(savingsAccount1.getPrimaryOwner().getName()));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(savingsAccount1.getMinimumBalance().getAmount().toString()));
    }
}



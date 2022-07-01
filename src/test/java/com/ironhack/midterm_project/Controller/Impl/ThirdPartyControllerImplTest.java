package com.ironhack.midterm_project.Controller.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm_project.Controller.DTO.DTOCreateThirdPartyUser;
import com.ironhack.midterm_project.Model.Embedded.Address;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Model.Users.Role;
import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.RoleRepository;
import com.ironhack.midterm_project.Repository.ThirdPartyUserRepository;
import com.ironhack.midterm_project.Repository.UserRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ThirdPartyUserRepository thirdPartyUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Role adminRole;
    private Role userRole;

    private User adminUser;
    private AccountHolder accountHolder;

    private Address address1 = new Address("Menorca Street","Madrid","Spain","28009");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        adminRole = new Role("ADMIN");
        userRole = new Role("USER");

        adminUser = new User("admin",passwordEncoder.encode("123456"),"Sofia de la Fuente");
        adminRole.setUser(adminUser);

        accountHolder = new AccountHolder("sofidelaf",passwordEncoder.encode("123456"),"Sofia de la Fuente", LocalDate.of(1998,11,1),address1);
        userRole.setUser(accountHolder);

        userRepository.saveAll(List.of(adminUser));
        accountHolderRepository.saveAll(List.of(accountHolder));
        roleRepository.saveAll(List.of(adminRole,userRole));
    }

    @AfterEach
    void tearDown() {
        thirdPartyUserRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAll_invalidAccess() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/third_party_users"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/third_party_users")
                        .with(httpBasic("sofidelaf","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void getAll_validAccess() throws Exception {

        MvcResult mvcResult2 = mockMvc.perform(get("/third_party_users")
                        .with(httpBasic("admin","123456"))
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void create_invalidAccess() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/third_party_users"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(post("/third_party_users")
                        .with(httpBasic("sofidelaf","123456"))
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void create_validAccess_Conflict() throws Exception {
        DTOCreateThirdPartyUser dtoCreateThirdPartyUser = new DTOCreateThirdPartyUser("sofidelaf","123456","Sofia de la Fuente","123456");
        String body = objectMapper.writeValueAsString(dtoCreateThirdPartyUser);

        MvcResult mvcResult2 = mockMvc.perform(post("/third_party_users")
                        .with(httpBasic("admin","123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    void create_validAccess_isCreated() throws Exception {
        DTOCreateThirdPartyUser dtoCreateThirdPartyUser = new DTOCreateThirdPartyUser("sofidelaf2","123456","Sofia de la Fuente","123456");
        String body = objectMapper.writeValueAsString(dtoCreateThirdPartyUser);


        MvcResult mvcResult2 = mockMvc.perform(post("/third_party_users")
                        .with(httpBasic("admin","123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult2.getResponse().getContentAsString().contains("sofidelaf2"));
        assertTrue(mvcResult2.getResponse().getContentAsString().contains("Sofia de la Fuente"));
        assertTrue(mvcResult2.getResponse().getContentAsString().contains("123456"));
    }
}
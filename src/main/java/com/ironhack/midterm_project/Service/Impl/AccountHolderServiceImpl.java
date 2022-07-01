package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Model.Embedded.Address;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Model.Users.Role;
import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.RoleRepository;
import com.ironhack.midterm_project.Repository.UserRepository;
import com.ironhack.midterm_project.Service.Interface.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountHolderServiceImpl implements AccountHolderService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountHolder createAccountHolder(String username, String password, String name, Set<Role> roles, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {

        AccountHolder newAccountHolder = new AccountHolder();

        /** username duplicate validation**/
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"The username "+username + " already exist in the Database");
        }
        newAccountHolder.setUsername(username);

        /**set password **/
        newAccountHolder.setPassword(passwordEncoder.encode(password));

        /**name**/
        newAccountHolder.setName(name);

        /**roles**/
        newAccountHolder.setRoles(roles);


        /**dateOfBirth**/
        newAccountHolder.setDateOfBirth(dateOfBirth);

        /**primaryAddress**/
        newAccountHolder.setPrimaryAddress(primaryAddress);

        /**mailingAddress**/
        newAccountHolder.setMailingAddress(mailingAddress);

        /** check of existing account holder with same information**/
        for(AccountHolder accountHolder: accountHolderRepository.findAll()){
            if(accountHolder.equals(newAccountHolder) && accountHolder.getName().equals(name)){ //Equals override does not include the name that comes from User
                throw new ResponseStatusException(HttpStatus.CONFLICT, "There is a user with the same information");
            }
        }

        accountHolderRepository.save(newAccountHolder);

        /**roles**/
        for(Role role : roles){
            role.setUser(newAccountHolder);
            roleRepository.save(role);
        }

        return accountHolderRepository.findById(newAccountHolder.getId()).get();
    }
}


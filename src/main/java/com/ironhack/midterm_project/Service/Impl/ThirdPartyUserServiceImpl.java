package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Model.Users.Role;
import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;
import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.RoleRepository;
import com.ironhack.midterm_project.Repository.ThirdPartyUserRepository;
import com.ironhack.midterm_project.Repository.UserRepository;
import com.ironhack.midterm_project.Service.Interface.ThirdPartyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Service
public class ThirdPartyUserServiceImpl implements ThirdPartyUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ThirdPartyUserRepository thirdPartyUserRepository;

    private final String ROLE_NAME = "THIRD_PARTY_USER";

    public ThirdPartyUser create(String username, String password, String name, String hashedKey) {

        ThirdPartyUser newThirdPartyUser = new ThirdPartyUser();

        /** username duplicate validation**/
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"The username "+ username + " already exist in the Database");
        }
        newThirdPartyUser.setUsername(username);

        /**set password **/
        newThirdPartyUser.setPassword(passwordEncoder.encode(password));

        /**name**/
        newThirdPartyUser.setName(name);

        /**roles**/
        newThirdPartyUser.setRoles(Set.of(new Role(ROLE_NAME)));

        /**HashedKey**/
        newThirdPartyUser.setHashedKey(hashedKey);

        thirdPartyUserRepository.save(newThirdPartyUser);

        /**roles**/
        Role role = new Role(ROLE_NAME);
        role.setUser(newThirdPartyUser);
        roleRepository.save(role);

        roleRepository.save(role);

        return thirdPartyUserRepository.findById(newThirdPartyUser.getId()).get();
    }
}


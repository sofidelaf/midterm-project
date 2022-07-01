package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateThirdPartyUser;
import com.ironhack.midterm_project.Controller.Interface.ThirdPartyController;
import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;
import com.ironhack.midterm_project.Repository.ThirdPartyUserRepository;
import com.ironhack.midterm_project.Service.Interface.ThirdPartyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {

    @Autowired
    private ThirdPartyUserRepository thirdPartyUserRepository;

    @Autowired
    private ThirdPartyUserService thirdPartyUserService;

    @GetMapping("/third_party_users")
    @ResponseStatus(HttpStatus.OK)
    public List<ThirdPartyUser> getAll() {
        return thirdPartyUserRepository.findAll();
    }

    @PostMapping("/third_party_users")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdPartyUser create(@RequestBody DTOCreateThirdPartyUser dtoCreateThirdPartyUser) {

        return thirdPartyUserService.create(
                dtoCreateThirdPartyUser.getUsername(),
                dtoCreateThirdPartyUser.getPassword(),
                dtoCreateThirdPartyUser.getName(),
                dtoCreateThirdPartyUser.getHashedKey()
        );
    }
}


package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.Interface.UserController;
import com.ironhack.midterm_project.Model.Users.User;
import com.ironhack.midterm_project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        return userRepository.findAll();
    }
}

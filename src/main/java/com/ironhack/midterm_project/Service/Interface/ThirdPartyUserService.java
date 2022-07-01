package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;

public interface ThirdPartyUserService {
    ThirdPartyUser create(String username, String password, String name, String hashedKey);
}

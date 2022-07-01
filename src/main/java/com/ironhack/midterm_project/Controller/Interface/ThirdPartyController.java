package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateThirdPartyUser;
import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;

import java.util.List;

public interface ThirdPartyController {
    List<ThirdPartyUser> getAll();
    ThirdPartyUser create(DTOCreateThirdPartyUser dtoCreateThirdPartyUser);
}

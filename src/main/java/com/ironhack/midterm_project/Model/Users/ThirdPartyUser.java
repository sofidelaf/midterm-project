package com.ironhack.midterm_project.Model.Users;


import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class ThirdPartyUser extends User {

    private String hashedKey;


    // Constructor method

    public ThirdPartyUser() {
    }

    public ThirdPartyUser(String username, String password, String name, String hashedKey) {
        super(username, password, name);
        this.hashedKey = hashedKey;
    }

    // Getters and setters

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }

}


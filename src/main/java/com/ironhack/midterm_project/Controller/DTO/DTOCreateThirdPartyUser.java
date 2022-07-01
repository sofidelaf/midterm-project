package com.ironhack.midterm_project.Controller.DTO;

public class DTOCreateThirdPartyUser {

    private String username;

    private String password;

    private String name;

    private String hashedKey;

    // Constructor method

    public DTOCreateThirdPartyUser(String username, String password, String name, String hashedKey) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.hashedKey = hashedKey;
    }

    // Getters and setters


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}

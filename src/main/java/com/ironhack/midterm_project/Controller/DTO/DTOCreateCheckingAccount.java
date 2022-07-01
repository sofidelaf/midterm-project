package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Money;

public class DTOCreateCheckingAccount {

    private Long primaryOwnerUserId;

    private Long secondaryOwnerUserId;

    private Money initialBalance;

    private String secretKey;


    // Constructor method

    public DTOCreateCheckingAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey) {
        this.primaryOwnerUserId = primaryOwnerUserId;
        this.secondaryOwnerUserId = secondaryOwnerUserId;
        this.initialBalance = initialBalance;
        this.secretKey = secretKey;
    }

    // Getters and setters

    public Long getPrimaryOwnerUserId() {
        return primaryOwnerUserId;
    }

    public void setPrimaryOwnerUserId(Long primaryOwnerUserId) {
        this.primaryOwnerUserId = primaryOwnerUserId;
    }

    public Long getSecondaryOwnerUserId() {
        return secondaryOwnerUserId;
    }

    public void setSecondaryOwnerUserId(Long secondaryOwnerUserId) {
        this.secondaryOwnerUserId = secondaryOwnerUserId;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Money initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}

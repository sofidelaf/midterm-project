package com.ironhack.midterm_project.Model.Accounts;


import com.ironhack.midterm_project.Model.Embedded.Money;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name= "account_id")
public class CreditCardAccount extends Account {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",column = @Column(name = "credit_limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency"))
    })
    @NotNull
    private Money creditLimit;

    @NotNull
    @Column(precision = 5,scale = 4)
    private BigDecimal interestRate;

    private LocalDate dateLastInterestAdded;

    // Constructor method

    public CreditCardAccount() {
    }

    // Getters and setters

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getDateLastInterestAdded() {
        return dateLastInterestAdded;
    }

    public void setDateLastInterestAdded(LocalDate dateLastInterestAdded) {
        this.dateLastInterestAdded = dateLastInterestAdded;
    }
}

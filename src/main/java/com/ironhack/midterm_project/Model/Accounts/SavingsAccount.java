package com.ironhack.midterm_project.Model.Accounts;


import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name= "account_id")
public class SavingsAccount extends Account{

    @NotNull
    @Column(precision = 5,scale = 4)
    private BigDecimal interestRate;

    private LocalDate dateLastInterestAdded;

    public SavingsAccount() {
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate.setScale(4, RoundingMode.HALF_EVEN);
    }

    public LocalDate getDateLastInterestAdded() {
        return dateLastInterestAdded;
    }

    public void setDateLastInterestAdded(LocalDate dateLastInterestAdded) {
        this.dateLastInterestAdded = dateLastInterestAdded;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                ", interestRate=" + interestRate +
                ", dateLastInterestAdded=" + dateLastInterestAdded +
                '}';
    }
}

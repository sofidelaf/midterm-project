package com.ironhack.midterm_project.Model.Accounts;


import com.ironhack.midterm_project.Model.Embedded.Money;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name= "account_id")
public class CheckingAccount extends Account {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",column = @Column(name = "monthly_maintenance_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency"))
    })
    private Money monthlyMaintenanceFee;

    private LocalDate dateLastMonthlyMaintenanceFeeApplied;


    // Constructor

    public CheckingAccount() {
    }

    // Getters and setters

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public LocalDate getDateLastMonthlyMaintenanceFeeApplied() {
        return dateLastMonthlyMaintenanceFeeApplied;
    }

    public void setDateLastMonthlyMaintenanceFeeApplied(LocalDate dateLastMonthlyMaintenanceFeeApplied) {
        this.dateLastMonthlyMaintenanceFeeApplied = dateLastMonthlyMaintenanceFeeApplied;
    }


}
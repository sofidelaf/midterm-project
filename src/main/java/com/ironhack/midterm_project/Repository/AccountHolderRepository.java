package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder,Long> {
    Optional<AccountHolder> findByUsername(String username);
}
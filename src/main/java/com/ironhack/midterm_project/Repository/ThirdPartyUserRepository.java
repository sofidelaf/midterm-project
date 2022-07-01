package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyUserRepository extends JpaRepository<ThirdPartyUser, Long> {
    Optional<ThirdPartyUser> findByUsername(String username);
}
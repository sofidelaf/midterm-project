package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Users.Role;

import java.util.List;

public interface RoleRepository {
    void save(Role role);

    void saveAll(List<Role> adminRole);

    void deleteAll();
}

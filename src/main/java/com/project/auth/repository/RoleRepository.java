package com.project.auth.repository;


import com.project.auth.domain.Role;
import com.project.auth.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(RoleName roleName);
}
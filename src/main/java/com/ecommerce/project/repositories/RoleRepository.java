package com.ecommerce.project.repositories;

import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRoles appRoles);
}

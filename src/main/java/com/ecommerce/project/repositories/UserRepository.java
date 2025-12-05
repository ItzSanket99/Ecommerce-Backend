package com.ecommerce.project.repositories;

import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);

    boolean existsByUserName(@NotBlank @Size(min = 3,max = 20) String username);

    boolean existsByEmail(@NotBlank @Size(min = 3,max = 20) String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :role")
    Page<User> findByRoleName(@Param("role") AppRoles appRoles, Pageable pageable);
}

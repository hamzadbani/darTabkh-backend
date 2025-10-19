package com.darTabkh.darTabkh.repository;

import com.darTabkh.darTabkh.entity.Role;
import com.darTabkh.darTabkh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRole(Role role);
    List<User> findByRoleAndCity(Role role, String city);
}

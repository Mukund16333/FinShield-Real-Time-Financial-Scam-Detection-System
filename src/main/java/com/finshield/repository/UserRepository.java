package com.finshield.repository;

import com.finshield.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountId(String accountId);

    boolean existsByEmail(String email);

    boolean existsByAccountId(String accountId);
}


package com.busanit501.findmyfet.repository;

import com.busanit501.findmyfet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    // Dashboard stats
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}


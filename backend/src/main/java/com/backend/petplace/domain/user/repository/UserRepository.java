package com.backend.petplace.domain.user.repository;

import com.backend.petplace.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String name);

  boolean existsByEmail(String email);
}

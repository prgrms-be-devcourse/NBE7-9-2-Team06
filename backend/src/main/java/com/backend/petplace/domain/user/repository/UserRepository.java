package com.backend.petplace.domain.user.repository;

import com.backend.petplace.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String name);

  boolean existsByEmail(String email);

  Optional<User> findByNickName(String nickName);
}
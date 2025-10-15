package com.backend.petplace.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.user.entity.User;

public interface UserRepository  extends JpaRepository<User, Long> {
}

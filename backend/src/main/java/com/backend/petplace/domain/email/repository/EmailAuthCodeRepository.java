package com.backend.petplace.domain.email.repository;

import com.backend.petplace.domain.email.entity.EmailAuthCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthCodeRepository extends JpaRepository<EmailAuthCode, String> {

  Optional<EmailAuthCode> findByEmailAndAuthCode(String email, String authCode);

  Optional<EmailAuthCode> findByEmail(String email);
}

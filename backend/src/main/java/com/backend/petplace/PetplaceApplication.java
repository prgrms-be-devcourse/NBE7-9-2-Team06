package com.backend.petplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PetplaceApplication {

  public static void main(String[] args) {

    SpringApplication.run(PetplaceApplication.class, args);
  }
}

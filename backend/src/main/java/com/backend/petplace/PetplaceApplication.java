package com.backend.petplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
//@EnableScheduling  // 스케줄러 사용 시 주석 해제
public class PetplaceApplication {

  public static void main(String[] args) {

    SpringApplication.run(PetplaceApplication.class, args);
  }
}

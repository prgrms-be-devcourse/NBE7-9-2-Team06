package com.backend.petplace.domain.mypage.dto;

import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.entity.Pet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageUserPets {

  private final Long id;
  private final String name;
  private final Gender gender;
  private final LocalDate birthDate;
  private final String type;

  public MyPageUserPets(Long id, String name, Gender gender, LocalDate birthDate, String type) {
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }

}

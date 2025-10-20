package com.backend.petplace.domain.pet.dto;

import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.entity.Pet;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "반려동물 정보")
public class PetInfo { //읽기 전용 클래스

  private final Long id;
  private final String name;
  private final Gender gender;
  private final LocalDate birthDate;
  private final String type;

  @Builder
  public PetInfo(Long id, String name, Gender gender, LocalDate birthDate, String type) {
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }

  public static PetInfo from(Pet pet){ //마이페이지 도메인에서 사용할 Read
    return PetInfo.builder()
        .id(pet.getId())
        .name(pet.getName())
        .gender(pet.getGender())
        .birthDate(pet.getBirthDate())
        .type(pet.getType())
        .build();
  }
}

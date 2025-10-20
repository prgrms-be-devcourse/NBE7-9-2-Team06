package com.backend.petplace.domain.pet.dto.response;

import com.backend.petplace.domain.pet.dto.PetInfo;
import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.entity.Pet;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "반려동물 생성 확인용 응답 데이터")
public class CreatePetResponse { //생성 시 반환용 클래스

  private final Long id;
  private final String name;
  private final Gender gender;
  private final LocalDate birthDate;
  private final String type;

  @Builder
  public CreatePetResponse(Long id, String name, Gender gender, LocalDate birthDate, String type) {
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }

  public static CreatePetResponse from(Pet pet){
    return CreatePetResponse.builder()
        .id(pet.getId())
        .name(pet.getName())
        .gender(pet.getGender())
        .birthDate(pet.getBirthDate())
        .type(pet.getType())
        .build();
  }

}

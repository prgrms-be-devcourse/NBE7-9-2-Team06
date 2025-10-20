package com.backend.petplace.domain.pet.dto.response;

import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.entity.Pet;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePetResponse {

  private final Long id;
  private final String name;
  private final Gender gender;
  private final LocalDate birthDate;
  private final String type;

  @Builder
  public UpdatePetResponse(Long id, String name, Gender gender, LocalDate birthDate, String type) {
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }

  public static UpdatePetResponse from(Pet pet){
    return UpdatePetResponse.builder()
        .id(pet.getId())
        .name(pet.getName())
        .gender(pet.getGender())
        .birthDate(pet.getBirthDate())
        .type(pet.getType())
        .build();
  }

}

package com.backend.petplace.domain.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "반려동물 생성 요청 데이터")
public class CreatePetRequest {

  @NotBlank(message = "이름은 필수로 입력해야 합니다.")
  @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "특수문자는 입력할 수 없습니다.")
  @Schema(description = "반려동물 이름 (특수문자 불가)")
  private final String name;

  @NotBlank(message = "성별은 필수로 입력해야 합니다.")
  @Pattern(regexp = "Male|Female", message = "성별은, 남자, 여자 중 하나만 입력할 수 있습니다.")
  @Schema(description = "반려동물 성별 (남, 여 하나 선택 가능)")
  private final String gender;

  @Schema(description = "반려동물 생년월일 (모를 경우 빈칸)")
  private final LocalDate birthDate;

  @Schema(description = "반려동물 품종 (모를 경우 빈칸)")
  private final String type;

  public CreatePetRequest(String name, String gender, LocalDate birthDate, String type) {
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }
}

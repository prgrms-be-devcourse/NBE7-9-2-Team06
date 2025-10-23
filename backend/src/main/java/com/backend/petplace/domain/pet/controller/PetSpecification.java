package com.backend.petplace.domain.pet.controller;

import static com.backend.petplace.global.response.ErrorCode.MEMBER_ACCESS_DENIED;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PET;

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest;
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest;
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse;
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pet", description = "반려동물 API")
public interface PetSpecification {

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER})
  @Operation(summary = "반려동물 생성", description = "사용자의 반려동물 정보를 생성합니다. 이름과 성별은 필수로 주어져야 합니다.")
  public ResponseEntity<ApiResponse<CreatePetResponse>> createPet(
      @Parameter(description = "반려동물 정보 - 이름, 성별, 생년월일, 품종", required = true) CreatePetRequest request,
      CustomUserDetails user
  );

  @ApiErrorCodeExamples({MEMBER_ACCESS_DENIED, NOT_FOUND_PET})
  @Operation(summary = "반려동물 수정", description = "사용자의 반려동물 정보를 수정합니다. 수정하고 싶은 필드를 선택하여 데이터를 수정합니다.")
  public ResponseEntity<ApiResponse<UpdatePetResponse>> updatePet(
      @Parameter(description = "반려동물 ID") Long id,
      @Parameter(description = "업데이트 할 반려동물 정보 - 이름, 성별, 생년월일, 품종 (선택 수정)", required = true) UpdatePetRequest request,
      CustomUserDetails user
  );

  @ApiErrorCodeExamples({MEMBER_ACCESS_DENIED, NOT_FOUND_PET})
  @Operation(summary = "반려동물 삭제", description = "사용자의 반려동물 정보를 삭제합니다. 요청값이 요구되지 않습니다.")
  public ResponseEntity<ApiResponse<Void>> deletePet(
      @Parameter(description = "삭제할 반려동물 ID") Long id,
      CustomUserDetails user
  );

}

package com.backend.petplace.domain.pet.controller;

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest;
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest;
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse;
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse;
import com.backend.petplace.domain.pet.service.PetService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class PetController implements PetSpecification{

  private final PetService petService;

  @Override
  @PostMapping("/create-pet")
  public ResponseEntity<ApiResponse<CreatePetResponse>> createPet(@RequestBody @Valid CreatePetRequest request, @AuthenticationPrincipal CustomUserDetails user) {
    Long userid = user.getUserId();
    CreatePetResponse response = petService.createPet(userid, request);
    return ResponseEntity.ok(ApiResponse.create(response));
  }

  @Override
  @PatchMapping("/update-pet/{id}")
  public ResponseEntity<ApiResponse<UpdatePetResponse>> updatePet(@PathVariable("id") Long id, @RequestBody @Valid UpdatePetRequest request, @AuthenticationPrincipal CustomUserDetails user){
    Long userid = user.getUserId();
    UpdatePetResponse response = petService.updatePet(userid, id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  @DeleteMapping("/delete-pet/{id}")
  public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails user) {
    Long userid = user.getUserId();
    petService.deletePet(userid, id);
    return ResponseEntity.ok(ApiResponse.success());
  }
}

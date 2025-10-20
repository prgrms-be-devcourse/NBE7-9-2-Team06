package com.backend.petplace.domain.pet.controller;

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest;
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest;
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse;
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse;
import com.backend.petplace.domain.pet.service.PetService;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/create-pet")
  public ResponseEntity<ApiResponse<CreatePetResponse>> createPet(@RequestBody @Valid CreatePetRequest request) {
    //String userName = SecurityContextHolder.getContext().getAuthentication().getName(); - 로그인 유저 정보
    //로그인 구현되면 입력값 변경 예정
    String nickname = "test"; //로컬에서 만든 db 사용 (임시)
    CreatePetResponse response = petService.createPet(nickname, request);
    return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
  }

  @PatchMapping("/update-pet/{id}")
  public ResponseEntity<ApiResponse<UpdatePetResponse>> updatePet(@PathVariable("id") Long id, @RequestBody @Valid UpdatePetRequest request){
    String nickname = "test"; //로컬에서 만든 db 사용 (임시)
    UpdatePetResponse response = petService.updatePet(nickname, id, request);
    return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
  }

  @DeleteMapping("/delete-pet/{id}")
  public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable("id") Long id) {
    String nickname = "test"; //로컬에서 만든 db 사용 (임시)
    petService.deletePet(nickname, id);
    return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
  }
}

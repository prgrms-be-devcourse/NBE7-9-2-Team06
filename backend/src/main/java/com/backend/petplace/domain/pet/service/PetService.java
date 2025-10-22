package com.backend.petplace.domain.pet.service;

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest;
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest;
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse;
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse;
import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.entity.Pet;
import com.backend.petplace.domain.pet.repository.PetRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

  private final PetRepository petRepository;
  private final UserRepository userRepository;

  private User findUser(Long userid){
    return userRepository.findById(userid)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private void validUser(Long userId, Long validationId){
    if(!userId.equals(validationId)) {
      throw new BusinessException(ErrorCode.MEMBER_ACCESS_DENIED);
    }
  }

  @Transactional
  public CreatePetResponse createPet(Long userid, CreatePetRequest request){
    User user = findUser(userid);
    Pet pet = Pet.createPet(user, request);
    pet.assignUser(user);

    petRepository.save(pet);
    return CreatePetResponse.from(pet);
  }

  @Transactional
  public UpdatePetResponse updatePet(Long userid, Long id, UpdatePetRequest request){
    User user = findUser(userid);
    Pet pet = petRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PET));
    validUser(user.getId(), pet.getUser().getId());

    pet.updatePet(request);

    return UpdatePetResponse.from(pet);
  }

  @Transactional
  public void deletePet(Long userid, Long id){
    User user = findUser(userid);
    Pet pet = petRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PET));
    validUser(user.getId(), pet.getUser().getId());

    if(pet.isActivated()){
      pet.unActivated();
    }else{
      throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }
  }

}

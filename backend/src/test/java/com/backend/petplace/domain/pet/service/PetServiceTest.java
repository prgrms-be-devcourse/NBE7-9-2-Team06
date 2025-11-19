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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest //단위테스트를 위해 넣었습니다. 통합 테스트 시 SpringBootTest를 넣으시면 됩니다.
@Import(PetService.class) //DataJpaTest는 repository, entitymanager만 빈으로 등록합니다. service빈은 따로 넣어야함.
@Transactional
class PetServiceTest {

  @Autowired
  private PetService petService;

  @Autowired
  private PetRepository petRepository;

  @Autowired
  private UserRepository userRepository;

  @PersistenceContext
  private EntityManager em;

  private User user;

  @BeforeEach
  void init(){
    user = User.builder()
        .nickName("user1")
        .email("user1@example.com")
        .password("pwd")
        .address("Address")
        .zipcode("11111")
        .build();
    userRepository.save(user);
  }

  @Test
  @DisplayName("펫 생성")
  void createPet() { //펫 생성

    //given
    CreatePetRequest request = new CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈"); //요청

    //when
    CreatePetResponse response = petService.createPet(user.getId(),request);
    Pet saved = petRepository.findById(response.getId()).orElseThrow();

    //then
    Assertions.assertThat("뚜뚜").isEqualTo(saved.getName());
    Assertions.assertThat(Gender.Female).isEqualTo(saved.getGender());
    Assertions.assertThat(LocalDate.of(2022, 1, 1)).isEqualTo(saved.getBirthDate());
    Assertions.assertThat("말티즈").isEqualTo(saved.getType());
    Assertions.assertThat(user.getId()).isEqualTo(saved.getUser().getId());
  }

  @Test
  @DisplayName("펫 수정")
  void updatePet() { //펫 수정
    //given
    CreatePetRequest createRequest = new CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈"); //요청 (생성)
    CreatePetResponse createPetResponse = petService.createPet(user.getId(), createRequest); //응답

    //when
    Pet saved = petRepository.findById(createPetResponse.getId()).orElseThrow();

    UpdatePetRequest updatePetRequest = new UpdatePetRequest("두두", Gender.Male.toString(), LocalDate.of(2021, 1, 1), "웰시코기"); //요청 (수정)
    petService.updatePet(user.getId(), saved.getId(), updatePetRequest);

    em.flush(); //트랜잭션 안 끝났으므로 쿼리 업데이트
    em.clear(); //영속성 컨텍스트 초기화

    Pet updated = petRepository.findById(saved.getId()).orElseThrow(); //업데이된 db 꺼내기

    //then
    Assertions.assertThat("두두").isEqualTo(updated.getName());
    Assertions.assertThat(Gender.Male).isEqualTo(updated.getGender());
    Assertions.assertThat(LocalDate.of(2021, 1, 1)).isEqualTo(updated.getBirthDate());
    Assertions.assertThat("웰시코기").isEqualTo(updated.getType());
    Assertions.assertThat(user.getId()).isEqualTo(updated.getUser().getId());

  }

  @Test
  @DisplayName("펫 삭제")
  void deletePet() { //펫 삭제
    //given
    CreatePetRequest request = new CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈"); //요청

    //when
    CreatePetResponse response = petService.createPet(user.getId(),request);
    Pet saved = petRepository.findById(response.getId()).orElseThrow();

    petService.deletePet(user.getId(), saved.getId());

    em.flush();
    em.clear();

    Pet deleted = petRepository.findById(saved.getId()).orElseThrow();
    Assertions.assertThat(deleted.isActivated()).isFalse();
  }
}
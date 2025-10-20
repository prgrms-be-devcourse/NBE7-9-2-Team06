package com.backend.petplace.domain.pet.entity;

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest;
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "petId")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Column(name = "name")
  private String name;

  @Column(name = "gender", nullable = false)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(name = "birthDate")
  private LocalDate birthDate;

  @Column(name = "type")
  private String type;

  @Builder
  public Pet(User user, String name, Gender gender, LocalDate birthDate, String type) {
    this.user = user;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.type = type;
  }

  public static Pet createPet(User user, CreatePetRequest request){
    return Pet.builder()
        .user(user)
        .name(request.getName())
        .gender(Gender.valueOf(request.getGender()))
        .birthDate(request.getBirthDate())
        .type(request.getType())
        .build();
  }

  public void assignUser(User user){ //엔티티 생성 이후 한번씩 등록해주기 (연관관계 편의 메서드)
    if(this.user != null){
      user.getPets().remove(this);
    }
    this.user = user; //user FK 적용
    user.getPets().add(this); //반대쪽에도 똑같이 동기화
  }

  public void updatePet(UpdatePetRequest request){ //업데이트 (선택적 업데이트)
    this.name = !request.isValidName() ? this.name : request.getName();
    this.gender = request.getGender() == null ? this.gender : Gender.valueOf(request.getGender());
    this.type = request.getType() == null ? this.type : request.getType();
    this.birthDate = request.getBirthDate() == null ? this.birthDate : request.getBirthDate();
  }

}

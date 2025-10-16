package com.backend.petplace.domain.user.entity;

import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private Long id;

  @Column(unique = true, nullable = false, name = "userEmail")
  private String email;

  @Column(nullable = false) // 암호화
  private String password;

  @Column(unique = true, nullable = false, name = "userName")
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String zipcode;

  private String addressDetail;

  private Integer totalPoint;

  @Builder
  public User(String name, String password, String email, String address,
      String zipcode, String addressDetail) {

    this.name = name;
    this.password = password;
    this.email = email;
    this.address = address;
    this.zipcode = zipcode;
    this.addressDetail = addressDetail;
    this.totalPoint = 0;
  }
}

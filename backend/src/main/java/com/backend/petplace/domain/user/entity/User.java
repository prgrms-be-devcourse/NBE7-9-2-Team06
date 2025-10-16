package com.backend.petplace.domain.user.entity;

import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
<<<<<<< HEAD
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private Long id;

  @Column(unique = true, nullable = false)
  private String userEmail;

  @Column(nullable = false) // 암호화
  private String password;

  @Column(unique = true, nullable = false)
  private String userName;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String zipcode;

  private String addressDetail;

  private Integer totalPoint;
=======
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
>>>>>>> f53c08b29f21e7962bf9ab0b704c850153a5084c
}

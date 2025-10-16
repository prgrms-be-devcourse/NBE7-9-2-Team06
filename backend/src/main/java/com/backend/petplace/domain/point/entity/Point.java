package com.backend.petplace.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Point {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
}

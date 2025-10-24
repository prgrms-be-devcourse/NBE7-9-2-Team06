package com.backend.petplace.domain.order.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomOrderRepositoryImpl implements CustomOrderRepository {
  private final EntityManager em;

  @Override
  public void clear() {
    em.clear();
  }
}
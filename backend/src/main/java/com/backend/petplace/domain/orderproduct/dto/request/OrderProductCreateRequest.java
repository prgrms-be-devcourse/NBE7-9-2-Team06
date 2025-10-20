package com.backend.petplace.domain.orderproduct.dto.request;

import lombok.Getter;

@Getter
public class OrderProductCreateRequest {
  Long productId;
  int quantity;
}

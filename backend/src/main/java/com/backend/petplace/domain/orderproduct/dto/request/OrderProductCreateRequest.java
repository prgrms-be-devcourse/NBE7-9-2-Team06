package com.backend.petplace.domain.orderproduct.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductCreateRequest {
  Long productId;
  int quantity;
}

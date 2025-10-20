package com.backend.petplace.domain.order.service;

import static com.backend.petplace.domain.orderproduct.entity.OrderProduct.createOrderProduct;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.repository.OrderRepository;
import com.backend.petplace.domain.orderproduct.dto.request.OrderProductCreateRequest;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import com.backend.petplace.domain.orderproduct.repository.OrderProductRepository;
import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final OrderProductRepository orderProductRepository;

  @Transactional
  public void createOrder(OrderCreateRequest request, Long userId) {
    User user = createUser(userId);

    Order order = Order.createOrder(user, request.getTotalPrice());
    orderRepository.save(order);

    for (OrderProductCreateRequest orderProductCreateRequest : request.getOrderProducts()) {

      Product product = createProduct(orderProductCreateRequest);

      OrderProduct orderProduct = createOrderProduct(
          order,
          product,
          orderProductCreateRequest.getQuantity()
      );

      orderProductRepository.save(orderProduct);
    }
  }

  private User createUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private Product createProduct(OrderProductCreateRequest orderProductCreateRequest) {
    return productRepository
        .findById(orderProductCreateRequest.getProductId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));
  }
}

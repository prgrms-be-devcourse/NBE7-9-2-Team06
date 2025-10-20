package com.backend.petplace.domain.order.service;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.entity.OrderStatus;
import com.backend.petplace.domain.order.repository.OrderRepository;
import com.backend.petplace.domain.orderproduct.dto.request.OrderProductCreateRequest;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  @Transactional
  public void createOrder(OrderCreateRequest request, Long userId) {

    User user = createUser(userId);

    checkLackOfPoint(user, request.getTotalPrice());

    Order order = Order.createOrder(user, request.getTotalPrice());
    orderRepository.save(order);

    user.addOrder(order);

    List<OrderProduct> orderProducts = putOrderProducts(request, order);
    order.addOrderProducts(orderProducts);

    orderRepository.save(order);
  }

  private User createUser(Long userId) {

    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private void checkLackOfPoint(User user, Long totalPrice) {

    if (user.getTotalPoint() < totalPrice) {
      throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
    }
  }

  private List<OrderProduct> putOrderProducts(
      OrderCreateRequest request, Order order
  ) {

    return request.getOrderProducts().stream()
        .map(orderProductRequest -> {

          Product product = productRepository.findById(orderProductRequest.getProductId())
              .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));

          checkLackOfStock(orderProductRequest, product);

          return OrderProduct.createOrderProduct(
              order, product, orderProductRequest.getQuantity()
          );
        })
        .collect(Collectors.toList());
  }

  private void checkLackOfStock(
      OrderProductCreateRequest orderProductRequest, Product product
  ) {
    if (orderProductRequest.getQuantity() > product.getStock()) {
      throw new BusinessException(ErrorCode.NOT_ENOUGH_STOCK);
    }
  }

  public List<OrderReadByIdResponse> getOrdersByUserId(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);

    return orders.stream()
        .map(OrderReadByIdResponse::new) // Order -> DTO 변환
        .toList();
  }

  public void cancelOrder(Long userId, Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER));

    if (order.getOrderStatus() != OrderStatus.ORDERED) {
      throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
    }

    order.cancelOrder();

    orderRepository.save(order);
  }

  @Transactional
  public void updateAllOrderStatus() {

    List<Order> orders = orderRepository.findByOrderStatus(OrderStatus.ORDERED);

    for (Order order : orders) {
      order.setOrderStatusDelivered();
    }
  }
}

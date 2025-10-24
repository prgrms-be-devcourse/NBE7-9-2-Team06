package com.backend.petplace.global.initdata;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.repository.OrderRepository;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseInitData implements CommandLineRunner {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;

  @Override
  public void run(String... args) {

    Product p1 = Product.builder()
        .productName("상품 1")
        .price(1L)
        .stock(9999L)
        .description("상품 1")
        .build();

    Product p2 = Product.builder()
        .productName("상품 2")
        .price(80L)
        .stock(9999L)
        .description("상품 2")
        .build();

    Product p3 = Product.builder()
        .productName("상품 3")
        .price(120L)
        .stock(9999L)
        .description("상품 3")
        .build();

    Product p4 = Product.builder()
        .productName("상품 4")
        .price(200L)
        .stock(9999L)
        .description("상품 4")
        .build();

    // DB에 저장
    productRepository.save(p1);
    productRepository.save(p2);
    productRepository.save(p3);
    productRepository.save(p4);

    // 스케줄러 테스트용
    // 유저 1번 사용
    UserSignupRequest userInfo = new UserSignupRequest(
        "testUser",
        "1111",
        "testUser1@naver.com",
        "10001",
        "310-5, Stone Street, Manhattan, NY, USA",
        "10001",
        "101"
    );

    User user = userRepository.findById(1L).orElseGet(() ->
        User.create(userInfo, "encodedPassword"));

    // 상품 1번만 사용
    Product product = productRepository.findById(1L).orElseThrow(() ->
        new BusinessException(ErrorCode.NOT_FOUND_PRODUCT)
    );

    long orderCount = orderRepository.count();
    if (orderCount == 0) {
      List<Order> orders = new ArrayList<>();

      //필요하면 숫자를 늘려주세요.
      //권장: 0 ~ 1
      for (int i = 0; i < 0; i++) {
        // order 생성
        Order order = Order.createOrder(user, 0);
        orders.add(order);

        // orderProduct 생성
        OrderProduct orderproduct = OrderProduct.createOrderProduct(order, product, 1L);
        order.addOrderProducts(List.of(orderproduct));
      }

      // 저장: 한번에
      orderRepository.saveAll(orders);

    }
  }
}

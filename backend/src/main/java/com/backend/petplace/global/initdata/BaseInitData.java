package com.backend.petplace.global.initdata;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.repository.OrderRepository;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
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
        .price(150L)
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

    // 스케줄러 테스트용 주문 생성
    // 유저 생성
    User user = userRepository.findById(1L).orElseGet(() -> {
      User newUser = User.createUser("John1234", "John1234@@", "John1234@naver.com",
          "서울 송파구 뮁뮁로 10", "zipcode", "301동 1004호");
      return userRepository.save(newUser);
    });

    // 상품 생성
    Product product = productRepository.findById(1L).orElseGet(() -> {
      Product newProduct = Product.createProduct("상품1", 1L, 10000L, "상품1.");
      return productRepository.save(newProduct);
    });

    // 10개의 주문 생성
    for (int numberOfOrder = 0; numberOfOrder < 10; numberOfOrder++) {
      Order order = Order.createOrder(user, 1);
      OrderProduct orderProduct = OrderProduct.createOrderProduct(order, product, 1L);
      List<OrderProduct> orderProducts = new ArrayList<>();
      orderProducts.add(orderProduct);
      order.addOrderProducts(orderProducts);
      orderRepository.save(order);
      orderRepository.flush();
    }
  }
}

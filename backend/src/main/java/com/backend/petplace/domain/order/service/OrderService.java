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
import com.backend.petplace.global.scheduler.executiontimer.ExecutionTimer;
import com.backend.petplace.global.scheduler.managementfactory.MemoryMonitor;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  @Transactional
  public long createOrder(OrderCreateRequest request, Long userId) {

    // 기존 유저 읽어서 객체 생성
    User user = readUser(userId);

    // 포인트 부족 시 예외 처리
    // 포인트 충분 시 주문 생성
    checkLackOfPoint(user, request.getTotalPrice());
    Order order = Order.createOrder(user, request.getTotalPrice());

    // 연관관계 위해 유저에 주문 추가
    user.addOrder(order);

    // 주문 상품들 생성 및 주문에 추가
    List<OrderProduct> orderProducts = addNewOrderProducts(request, order);
    order.addOrderProducts(orderProducts);

    // 총 가격만큼 유저 포인트 차감
    user.abstractPoints(request.getTotalPrice());

    // 저장
    orderRepository.save(order);

    return order.getOrderId();
  }

  private User readUser(Long userId) {

    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private void checkLackOfPoint(User user, Integer totalPrice) {

    if (user.getTotalPoint() < totalPrice) {
      throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
    }
  }

  private List<OrderProduct> addNewOrderProducts(OrderCreateRequest request, Order order) {

    return request.getOrderProducts().stream()
        .map(orderProductRequest -> addNewOrderProduct(orderProductRequest, order))
        .toList();
  }

  private OrderProduct addNewOrderProduct(OrderProductCreateRequest orderProductRequest, Order order) {

    Product product = productRepository.findById(orderProductRequest.getProductId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));

    // 재고가 충분하면 return, 부족하면 예외처리
    checkLackOfStock(orderProductRequest, product);
    return OrderProduct.createOrderProduct(order, product, orderProductRequest.getQuantity());
  }

  private void checkLackOfStock(OrderProductCreateRequest orderProductRequest, Product product) {

    if (orderProductRequest.getQuantity() > product.getStock()) {
      throw new BusinessException(ErrorCode.NOT_ENOUGH_STOCK);
    }
  }

  public List<OrderReadByIdResponse> getOrdersByUserId(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);

    // Order 객체를 DTO로 변환 후 반환
    return orders.stream()
        .map(OrderReadByIdResponse::new)
        .toList();
  }

  public void cancelOrder(Long userId, Long orderId) {

    // 주문이 존재하면 조회, 없으면 예외처리
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER));

    // 요청이 ORDERED 상태가 아니면 예외처리
    if (order.getOrderStatus() != OrderStatus.ORDERED) {
      throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
    }

    // 주문 취소 상태로 저장
    order.cancelOrder();
    orderRepository.save(order);

    // 주문 금액만큼 유저 포인트 복구
    User user = order.getUser();
    user.addPoints(order.getTotalPrice());
    userRepository.save(user);
  }

  public Integer getUserPoints(Long userId) {
    return userRepository.findById(userId)
        .map(User::getTotalPoint)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }


  // 한 배치 크기 (예: 500개)
  private static final int BATCH_SIZE = 500;
  // 동시에 처리할 스레드 수
  private static final int THREAD_COUNT = 4;

  // AOP Timer을 통한 메서드 실행 시간 측정용 어노테이션
  @ExecutionTimer
  // 여러가지 사용량 측정용 어노테이션
  @MemoryMonitor
  @Transactional
  public void updateAllOrderStatus() {
    log.info("DB에서 orders 받아오기...");
    List<Order> orders = orderRepository.findByOrderStatus(OrderStatus.ORDERED);
    log.info("조회 완료: {}개 주문", orders.size());

    // 배치 단위로 쪼개기
    List<List<Order>> batches = partition(orders, BATCH_SIZE);

    // 스레드 풀 생성
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    List<Future<?>> futures = new ArrayList<>();

    log.info("멀티스레드로 주문 상태 변경 시작 ({} batches, {} threads)", batches.size(), THREAD_COUNT);

    for (List<Order> batch : batches) {
      futures.add(executor.submit(() -> processBatch(batch)));
    }

    // 모든 작업 완료 대기
    for (Future<?> future : futures) {
      try {
        future.get(); // 예외 발생 시 throw
      } catch (Exception e) {
        log.error("배치 처리 중 오류 발생", e);
      }
    }

    executor.shutdown();
    log.info("모든 주문 상태 변경 완료");
  }

  // 각 배치를 별도 트랜잭션으로 처리
  @Transactional
  public void processBatch(List<Order> batch) {
    for (Order order : batch) {
      order.setOrderStatusDelivered();
    }

    // 영속성 컨텍스트 flush + clear로 메모리 절약
    orderRepository.flush();
    orderRepository.clear(); // JpaRepository에 직접 clear()가 없으면 EntityManager로
    log.info("배치 {}건 처리 완료", batch.size());
  }

  private List<List<Order>> partition(List<Order> list, int size) {
    List<List<Order>> parts = new ArrayList<>();
    for (int i = 0; i < list.size(); i += size) {
      parts.add(list.subList(i, Math.min(list.size(), i + size)));
    }
    return parts;
  }
}

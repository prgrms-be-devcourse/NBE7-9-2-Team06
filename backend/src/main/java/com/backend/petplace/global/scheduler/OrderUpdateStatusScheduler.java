package com.backend.petplace.global.scheduler;

import com.backend.petplace.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderUpdateStatusScheduler {

  private final OrderService orderService;

  //필요할 때 주석 제거하시면 됩니다.
  //@Scheduled(fixedRate = 1000 * 60 * 1) //1분마다 실행
  public void updateOrderStatusEveryDay() {
    orderService.updateAllOrderStatus();
  }
}

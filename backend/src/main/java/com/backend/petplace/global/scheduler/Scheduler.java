package com.backend.petplace.global.scheduler;

import com.backend.petplace.domain.order.service.OrderService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

  private final OrderService orderService;

  @Scheduled(fixedRate = 1000 * 60 * 1) //1분마다 실행
  public void updateOrderStatusEveryDay() {
    orderService.updateAllOrderStatus();
  }
}

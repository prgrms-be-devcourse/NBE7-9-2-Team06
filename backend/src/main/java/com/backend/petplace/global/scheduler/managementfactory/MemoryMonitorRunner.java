package com.backend.petplace.global.scheduler.managementfactory;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// 어노테이션 설명
//   Slf4j: 로그 위한 롬복 어노테이션
//   Aspect: AOP. 스프링이 런타임 때 이 메서드를 다른 Bean 메서드 실행 과정에 끼워넣는다 선언.
//           끼워넣는다 = 병렬인 건 X. 그냥 대상 메서드 전후로 추가 실행 (순차 실행).
//           이걸 "프록시 기반 실행 흐름을 만든다"라고 표현.
//   Component: 스프링 빈으로 등록하기 위한 어노테이션.
@Slf4j
@Aspect
@Order(1)//가장 먼저 실행
@Component
public class MemoryMonitorRunner {

  // 변수 정의
  //   heapSamples: 힙 사용량 샘플을 저장
  //   cpuSamples: cpu 사용량 샘플을 저장.
  //   running: 샘플링 스레드가 동작 중인지 표시.
  //     volatile?: 메인 스레드와 샘플링 중인 스레드 간 변수 값 공유한다.
  //       메인 스레드에서 running = false 하면 샘플링 중인 스레드도 false를 듣고 종료 가능.
  private final List<Long> heapSamples = new ArrayList<>();
  private final List<Double> cpuSamples = new ArrayList<>();
  private volatile boolean running = false;

  // 커넥션 풀 추적용 변수 정의
  private final HikariDataSource dataSource;
  public MemoryMonitorRunner(HikariDataSource dataSource) {
    this.dataSource = dataSource;  }


  // @Pointcut(): 언제 이 Aspect를 적용할 지 정함. 적용 기준은 () 속 파라미터. 이 코드의 핵심 포인트다.
  //   @annotation(~~~): ~~~.ExecutionTimer 커스텀 어노테이션이 붙은 대상 메서드가 실행 시작되는 시점 포착.
  //   즉, @ExecutionTimer 가 붙은 모든 메서드는, 시작 시 이 Aspect가 끼어들 수 있다.
  //
  // monitorHeap(): 이 @Pointcut의 이름을 monitor라 정의하는 용도.
  //   monitorHeap()를 부르면 @Pointcut(적용 시점)을 부르는 것.
  //   메서드로써는 아무 일 안함.
  @Pointcut("@annotation(com.backend.petplace.global.scheduler.managementfactory.MemoryMonitor)")
  public void monitorHeap() {
  }

  // @Around(): 위 @Pointcut(= timer())을 기준으로 감싸는(= 전/후 모두 관여 가능) 동작 수행.
  //   즉, 이 메서드 실행 前/中/後 모두 끼어들 수 있음.
  //
  // ExecutionTimeCheck(): @Around를 통해서 @ExecutionTimer가 붙은 메서드 실행 시 이 함수도 실행.
  //   실행 타이밍은 @Around에서 정의한 바에 따라 前/中/後 모두.
  //
  // ProceedingJoinPoint joinPoint: @ExecutionTimer가 붙은 대상 메서드의 실행 나타냄.
  //   즉, ExecutionTimerCheck 호출 시, 파라미터로 들어온 updateAllOrderStatus()가 실행됨. -> ?
  //
  // 의문: 대상 메서드인 updateAllOrderStatus()는 반환값 void인데 여기선 Object로 반환하는 이유?
  // 답: @Around 어노테이션은 원본 메서드의 반환값 그대로 돌려주게 설계되어 있어서.
  //   대상 메서드가 void면 null 반환.
  @Around("monitorHeap()")
  public Object monitorAround(ProceedingJoinPoint joinPoint) throws Throwable {

    // 1. 변수 초기화와 시작.
    //   초기화:
    //     heapSamples.clear()
    //     cpuSamples.clear()
    //     maxHeapused = {0} -> 왜 이런 구조여야만 하나?
    //       이유: 람다/익명 클래스에서 외부의 지역 변수 수정 시 final이어야 한다.
    //         근데 final이면 수정 불가.
    //         하지만 배열이면 '배열 요소'는 수정 가능.
    //         AtomicLong 써도 된다.
    //   시작:
    //     running = true
    heapSamples.clear();
    cpuSamples.clear();
    running = true;
    final long[] maxHeapUsed = {0};

    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
    com.sun.management.OperatingSystemMXBean osBean =
        (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    Map<String, Long> lastGcCount = new HashMap<>();
    Map<String, Long> lastGcTime = new HashMap<>();
    for (GarbageCollectorMXBean gc : gcBeans) {
      lastGcCount.put(gc.getName(), gc.getCollectionCount());
      lastGcTime.put(gc.getName(), gc.getCollectionTime());
    }

    // 2. 주기적으로 수치 샘플링
    // 별도 스레드에서 100ms 간격으로 수치 샘플링
    AtomicLong gcCountTotal = new AtomicLong();
    AtomicLong gcTimeTotal = new AtomicLong();
    Thread sampler = new Thread(() -> {
      try {

        int loopCounter = 1;
        while (running && !Thread.currentThread().isInterrupted()) {

          // 힙
          // 현재 힙 사용량은 heapSamples에 시간 순 저장
          // 최대 힙 사용량은 maxheapUsed[0]에 비교 후 갱신
          long used = memoryMXBean.getHeapMemoryUsage().getUsed();
          heapSamples.add(used);

          if (used > maxHeapUsed[0]) {
            maxHeapUsed[0] = used;
          }

          // CPU
          double processCpuLoad = osBean.getProcessCpuLoad();
          cpuSamples.add(processCpuLoad);

          // GC
          for (GarbageCollectorMXBean gc : gcBeans) {
            long deltaCount = gc.getCollectionCount() - lastGcCount.get(gc.getName());
            long deltaTime = gc.getCollectionTime() - lastGcTime.get(gc.getName());
            lastGcCount.put(gc.getName(), gc.getCollectionCount());
            lastGcTime.put(gc.getName(), gc.getCollectionTime());
            if (deltaCount > 0) {
              log.info("GC {} 발생: {}회, {}ms", gc.getName(), deltaCount, deltaTime);
              gcCountTotal.addAndGet(deltaCount);
              gcTimeTotal.addAndGet(deltaTime);
            }
          }

          // 활성 스레드와 힙 사용 현황
          int activeThreads = threadMXBean.getThreadCount();

          // HikariCP 풀 상태
          HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
          int active = poolMXBean.getActiveConnections();
          int idle = poolMXBean.getIdleConnections();
          int total = poolMXBean.getTotalConnections();
          int awaiting = poolMXBean.getThreadsAwaitingConnection();

          // 1초마다 현황 출력
          if (loopCounter % 10 == 0) {
            log.info("현재 출력: {} 번째 표시 \n"
                    + "    힙 사용량: {} MB, \n"
                    + "    활성 스레드: {}, \n"
                    + "    HikariCP - active: {}, \n"
                    + "    idle: {}, \n"
                    + "    total: {}, \n"
                    + "    awaiting: {} \n",
                loopCounter / 10, used / (1024 * 1024), activeThreads, active, idle, total, awaiting);
          }
          loopCounter++;

          Thread.sleep(100); // 100ms마다 샘플링 -> 오버헤드 위험하면 500~1000으로.
        }
      } catch (InterruptedException ignored) {
        // 스레드 종료
      }
    });

    // 2. 별도 스레드에서 메모리 샘플링 시작
    long methodStart = System.currentTimeMillis();
    sampler.start();

    // 2 - 5. 동시에, 본 스레드에선 updateAllUserStatus() 실행
    //   methodEnd: 대상 메서드인 updateAllUserStatus() 끝나면 종료 시간 저장.
    Object result = joinPoint.proceed();
    long methodEnd = System.currentTimeMillis();

    // 3. 대상 메서드 끝나고 별도 스레드도 종료 처리 및 시간 측정
    //     샘플러 시작 시간은 메서드 시작과 동일
    running = false; // 샘플링 종료
    sampler.join();  // 스레드 종료 대기

    // 4. 대상 메서드 다 끝나고 결과를 계산, 로그 출력
    double avgHeapUsed = heapSamples.stream().mapToLong(Long::longValue).average().orElse(0);
    double avgCpuLoad = cpuSamples.stream().mapToDouble(Double::doubleValue).average().orElse(0);

    log.info("메서드 {} \n"
            + "    메서드 실행 시간 (순수): {}ms, \n"
            + "    평균 힙 사용량: {} MB, \n"
            + "    최대 힙 사용량: {} MB, \n"
            + "    평균 CPU Load: {}% \n"
            + "    GC 총 발생 횟수: {}회 \n"
            + "    GC 총 발생 시간: {}ms \n",
        joinPoint.getSignature().getName(),
        (methodEnd - methodStart),
        avgHeapUsed / (1024 * 1024),
        maxHeapUsed[0] / (1024 * 1024),
        avgCpuLoad * 100,
        gcCountTotal,
        gcTimeTotal);

    return result;
  }

}

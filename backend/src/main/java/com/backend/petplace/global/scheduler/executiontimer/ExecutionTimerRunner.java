package com.backend.petplace.global.scheduler.executiontimer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


// 어노테이션 설명
//   Slf4j: 로그 위한 롬복 어노테이션
//   Aspect: AOP. 스프링이 런타임 때 이 메서드를 다른 Bean 메서드 실행 과정에 끼워넣는다 선언.
//           끼워넣는다 = 병렬인 건 X. 그냥 대상 메서드 전후로 추가 실행 (순차 실행).
//           이걸 "프록시 기반 실행 흐름을 만든다"라고 표현.
//   Component: 스프링 빈으로 등록하기 위한 어노테이션.
@Slf4j
@Aspect
@Order(Integer.MAX_VALUE) //가장 뒤에 실행: 순수하게 updateAllOrderStatus 재야해서. 먼저 실행하면 다른 aspect 체이닝 전처리/후처리 시간까지 찍힘.
@Component
public class ExecutionTimerRunner {

  // @Pointcut(): 언제 이 Aspect를 적용할 지 정함. 적용 기준은 () 속 파라미터. 이 코드의 핵심 포인트다.
  //   @annotation(~~~): ~~~.ExecutionTimer 커스텀 어노테이션이 붙은 대상 메서드가 실행 시작되는 시점 포착.
  //   즉, @ExecutionTimer 가 붙은 모든 메서드는, 시작 시 이 Aspect가 끼어들 수 있다.
  //
  // timer(): 이 @Pointcut의 이름을 timer라 정의하는 용도.
  //   timer()를 부르면 @Pointcut(적용 시점)을 부르는 것.
  //   메서드로써는 아무 일 안함.
  @Pointcut("@annotation(com.backend.petplace.global.scheduler.executiontimer.ExecutionTimer)")
  private void timer() {
  }

  // @Around(): 위 @Pointcut(= timer())을 기준으로 감싸는(= 전/후 모두 관여 가능) 동작 수행.
  //   즉, 이 메서드 실행 前/中/後 모두 끼어들 수 있음.
  //
  // executionTimeCheck(): @Around를 통해서 @ExecutionTimer가 붙은 메서드 실행 시 이 함수도 실행.
  //   실행 타이밍은 @Around에서 정의한 바에 따라 前/中/後 모두.
  //
  // ProceedingJoinPoint joinPoint: @ExecutionTimer가 붙은 대상 메서드의 실행 나타냄.
  //   즉, executionTimerCheck 호출 시, 파라미터로 들어온 updateAllOrderStatus()가 실행됨. -> ?
  //
  // 의문: 대상 메서드인 updateAllOrderStatus()는 반환값 void인데 여기선 Object로 반환하는 이유?
  // 답: @Around 어노테이션은 원본 메서드의 반환값 그대로 돌려주게 설계되어 있어서.
  //   대상 메서드가 void면 null 반환.
  @Around("timer()")
  public Object executionTimeCheck(ProceedingJoinPoint joinPoint) throws Throwable {

    // 1. 스톱워치 시작 후
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("ExecutionTimer: 스톱워치 시작");

    // 2. joinPoint로 들어온 updateAllOrderStatus()
    //   .proceed(): updateAllOrderStatus()를 실행.
    //   result: updateAllOrderStatus()의 반환값을 intercept해서 받음!
    Object result = joinPoint.proceed();

    // 3. 다 끝나서 result로 반환하면,
    //   스톱워치 멈춤.
    stopWatch.stop();
    log.info("ExecutionTimer: 스톱워치 정지");

    // 4. signature: updateAllOrderStatus() 메서드의 시그니처 정보를 받음.
    //   시그니처란?: 이름/파라미터/리턴타입 등 정보.
    //   methodName: 메서드 이름만 따로 저장.
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getMethod().getName();

    // 5. 총 실행 시간 로그로 출력.
    long totalTimeMillis = stopWatch.getTotalTimeMillis();
    log.info("실행 메서드: {} , \n"
            + "    순수하게 메서드만 실행한 시간 = {}ms \n",
        methodName, totalTimeMillis);

    // 6. joinPoint.proceed()에서 intercept한 updateAllOrderStatus()의 반환값을 반환.
    return result;
  }
}


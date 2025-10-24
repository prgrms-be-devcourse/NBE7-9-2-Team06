package com.backend.petplace.global.scheduler.executiontimer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 실행 시간 측정용 커스텀 어노테이션 정의
// 실행 시간 측정이란 횡단 괌심사 처리
//참고 링크: https://velog.io/@dhk22/Spring-AOP-%EA%B0%84%EB%8B%A8%ED%95%9C-AOP-%EC%A0%81%EC%9A%A9-%EC%98%88%EC%A0%9C-%EB%A9%94%EC%84%9C%EB%93%9C-%EC%8B%A4%ED%96%89%EC%8B%9C%EA%B0%84-%EC%B8%A1%EC%A0%95

// @Target: 어디에 어노테이션 붙일 수 있나 지정
//   - ElementType.METHOD: 메서드에 붙일 수 있음
//   - ElementType.ANNOTATION_TYPE: 다른 어노테이션에 붙일 수 있음 -> 필요 없음
//
// @Retention: 어노테이션 유지 정책 지정
//   - RetentionPolicy.RUNTIME: 런타임 시점까지 어노테이션 유지
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

// 실제 어노테이션 선언
public @interface ExecutionTimer {
}

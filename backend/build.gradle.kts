plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.backend"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// ✅ Spring Boot 기본 의존성
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")

	// ✅ DB (Database)
	runtimeOnly("com.h2database:h2")                // 로컬 테스트용
	runtimeOnly("com.mysql:mysql-connector-j")      // 실제 MySQL 연결용

	// ✅ Redis (Cache, Session, Message Queue)
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// ✅ AWS (S3 업로드 등)
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

	// ✅ JWT (인증/인가)
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// ✅ Swagger / API 문서화
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// ✅ Lombok / 개발 편의
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// WebClient
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// ✅ Test 관련
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // email (회원가입 인증)
    implementation ("org.springframework.boot:spring-boot-starter-mail")

	// 측정 관련
	//// Spring Boot Actuator
	implementation("org.springframework.boot:spring-boot-starter-actuator")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

package com.backend.petplace.global.config.security;

import com.backend.petplace.global.jwt.JwtAuthenticationFilter;
import com.backend.petplace.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults());

    http
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/api/v1/signup", "/api/v1/login").permitAll()
            .requestMatchers("/api/v1/signup-username", "/api/v1/signup-email", "/api/v1/email/auth").permitAll()
            .requestMatchers("/api/v1/places/{placeId}/reviews", "/api/v1/places/search", "api/v1/places/{placeId}").permitAll()
            .requestMatchers("/api/v1/presigned-url").permitAll()
            .requestMatchers("/api/v1/reviews").permitAll() // 프론트 jwt 연결 오류로 임시 허용 (백엔드 이상없음)

            // Spring Actuator API 허용
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        );

    http
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

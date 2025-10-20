package com.backend.petplace.global.jwt;

import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isPermitUrl(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);

    try {
      jwtTokenProvider.validateToken(token);

      Authentication authentication = jwtTokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (BusinessException ex) {
      response.setCharacterEncoding("UTF-8");
      response.setStatus(ex.getErrorCode().getStatus().value());
      ApiResponse<Void> apiResponse = ApiResponse.error(ex.getErrorCode());
      response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isPermitUrl(String path) {
    return path.startsWith("/h2-console")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/api/v1/login")
        || path.startsWith("/api/v1/signup");
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      logger.info("Bearer Token: " + bearerToken);
      return bearerToken.substring(7);
    }
    return null;
  }
}

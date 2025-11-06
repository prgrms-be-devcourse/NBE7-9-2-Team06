package com.backend.petplace.global.jwt;

import com.backend.petplace.domain.user.service.RedisService;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
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
  private final RedisService  redisService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = jwtTokenProvider.resolveToken(request);

    if (redisService.isBlackListed(token)) {
      sendErrorResponse(response, new BusinessException(ErrorCode.BLACKLIST_TOKEN));
      return;
    }

    if (token != null) {
      try {
        jwtTokenProvider.validateToken(token);

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (BusinessException ex) {
        sendErrorResponse(response, ex);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private static void sendErrorResponse(HttpServletResponse response, BusinessException ex)
      throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setStatus(ex.getErrorCode().getStatus().value());
    ApiResponse<Void> apiResponse = ApiResponse.error(ex.getErrorCode());
    response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
  }
}
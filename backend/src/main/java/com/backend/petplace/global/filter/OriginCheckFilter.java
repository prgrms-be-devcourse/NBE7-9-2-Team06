package com.backend.petplace.global.filter;

import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OriginCheckFilter implements Filter {

  private static final String ALLOWED_ORIGIN = "https://localhost:3001";
  private final ObjectMapper objectMapper;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
      chain.doFilter(request, response);
      return;
    }

    String origin = req.getHeader("Origin");
    String referer = req.getHeader("Referer");

    boolean valid = false;
    if (origin != null && origin.equals(ALLOWED_ORIGIN)) valid = true;
    else if (referer != null && referer.startsWith(ALLOWED_ORIGIN)) valid = true;

    if (!valid) {
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      res.setStatus(ErrorCode.ORIGIN_CHECK_INVALID.getStatus().value()); // 403
      res.getWriter().write(
          objectMapper.writeValueAsString(
              ApiResponse.error(ErrorCode.ORIGIN_CHECK_INVALID)
          )
      );
      return;
    }

    chain.doFilter(request, response);
  }
}
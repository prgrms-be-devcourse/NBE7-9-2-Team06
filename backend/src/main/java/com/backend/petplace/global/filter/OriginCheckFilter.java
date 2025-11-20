package com.backend.petplace.global.filter;

import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class OriginCheckFilter extends OncePerRequestFilter {

  private static final String ALLOWED_ORIGIN = "https://localhost:3001";
  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();

    // h2-console, swagger-ui, v3/api-docs 요청은 필터 제외
    return uri.startsWith("/h2-console")
        || uri.startsWith("/swagger-ui")
        || uri.startsWith("/v3/api-docs");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain)
      throws IOException, ServletException {

    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
      chain.doFilter(req, res);
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

    chain.doFilter(req, res);
  }
}
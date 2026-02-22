package edu.levytskyi.lab1api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    long startTime = System.currentTimeMillis();
    logger.info("Отримано вхідний запит: метод={}, URI={}", request.getMethod(), request.getRequestURI());

    try {
      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      logger.info("Завершено обробку запиту: URI={}, статус_відповіді={}, тривалість={}мс",
          request.getRequestURI(), response.getStatus(), duration);
    }
  }
}
package edu.levytskyi.lab1api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<Map<String, String>> handleExternalApiException(RestClientException ex) {
    logger.error("Відмова зовнішнього API: {}", ex.getMessage());

    Map<String, String> response = new HashMap<>();
    response.put("error", "Зовнішній сервіс тимчасово недоступний.");
    response.put("details", ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
    logger.error("Критична внутрішня помилка: {}", ex.getMessage(), ex);

    Map<String, String> response = new HashMap<>();
    response.put("error", "Внутрішня помилка сервера.");

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
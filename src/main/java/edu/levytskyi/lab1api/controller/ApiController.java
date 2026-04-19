package edu.levytskyi.lab1api.controller;

import edu.levytskyi.lab1api.entity.CityTranslation;
import edu.levytskyi.lab1api.repository.CityTranslationRepository;
import edu.levytskyi.lab1api.service.IntegrationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

  private final IntegrationService integrationService;
  private final CityTranslationRepository cityTranslationRepository;

  public ApiController(IntegrationService integrationService,
      CityTranslationRepository cityTranslationRepository) {
    this.integrationService = integrationService;
    this.cityTranslationRepository = cityTranslationRepository;
  }

  @GetMapping("/weather")
  public Object getWeather(@RequestParam(defaultValue = "Kyiv") String city) {
    return integrationService.getWeather(city);
  }

  @GetMapping("/db-check")
  public ResponseEntity<Map<String, Object>> checkDatabaseConnection() {
    try {
      // Рахуємо кількість записів у таблиці city_translation
      long count = cityTranslationRepository.count();
      return ResponseEntity.ok(Map.of(
          "status", "Успішно підключено до PostgreSQL!",
          "database", "PostgreSQL (Bitnami Chart)",
          "translationsCount", count,
          "message", "З'єднання встановлено, Hibernate ініціалізував схему."
      ));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of(
          "status", "Помилка підключення до БД",
          "error", e.getMessage()
      ));
    }
  }
}
package edu.levytskyi.lab1api.controller;

import edu.levytskyi.lab1api.service.IntegrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

  private final IntegrationService integrationService;

  public ApiController(IntegrationService integrationService) {
    this.integrationService = integrationService;
  }

  @GetMapping("/weather")
  public Object getWeather(@RequestParam(defaultValue = "Kyiv") String city) {
    return integrationService.getWeather(city);
  }

  @GetMapping("/currency")
  public Object getCurrency() {
    return integrationService.getCurrency();
  }
}
package edu.levytskyi.lab1api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IntegrationService {

  private static final Logger logger = LoggerFactory.getLogger(IntegrationService.class);
  private final RestTemplate restTemplate;

  @Value("${api.openweather.key}")
  private String openWeatherKey;

  @Value("${api.openweather.url}")
  private String openWeatherUrl;

  @Value("${api.currency.url}")
  private String currencyUrl;

  public IntegrationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable("weather")
  public Object getWeather(String city) {
    logger.info("Виконання HTTP-запиту до OpenWeather API для об'єкта: {}", city);
    String url = UriComponentsBuilder.fromUriString(openWeatherUrl)
        .queryParam("q", city)
        .queryParam("appid", openWeatherKey)
        .queryParam("units", "metric")
        .toUriString();

    return restTemplate.getForObject(url, Object.class);
  }

  @Cacheable("currency")
  public Object getCurrency() {
    logger.info("Виконання HTTP-запиту до API НБУ для отримання поточного курсу валют");
    return restTemplate.getForObject(currencyUrl, Object.class);
  }
}
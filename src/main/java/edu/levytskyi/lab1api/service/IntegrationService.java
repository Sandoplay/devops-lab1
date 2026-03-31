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

  @Value("${OPENWEATHER_API_KEY}")
  private String openWeatherKey;

  @Value("${api.openweather.url}")
  private String openWeatherUrl;


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

    return restTemplate.getForObject(url, Object.class);//як це тестити йолки палки
  }

}
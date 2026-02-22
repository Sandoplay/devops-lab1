package edu.levytskyi.lab1api.service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DiscordMessageListener extends ListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DiscordMessageListener.class);
  private final IntegrationService integrationService;

  // Регулярний вираз для української та англійської мов
  // Група 1: український запит (після "в"/"у")
  // Група 2: англійський запит (після "in")
  private static final Pattern WEATHER_PATTERN = Pattern.compile(
      "(?i).*(?:погод[ауі]\\s+[ву]|weather\\s+in)\\s+([а-яіїєґa-z-]+).*",
      Pattern.UNICODE_CHARACTER_CLASS
  );

  private static final Map<String, String> CITY_TRANSLATION_MAP = new HashMap<>();

  static {
    // Карта перекладу та нормалізації (Українська -> English для OpenWeather)
    CITY_TRANSLATION_MAP.put("києві", "Kyiv");
    CITY_TRANSLATION_MAP.put("київ", "Kyiv");
    CITY_TRANSLATION_MAP.put("львові", "Lviv");
    CITY_TRANSLATION_MAP.put("львів", "Lviv");
    CITY_TRANSLATION_MAP.put("одесі", "Odesa");
    CITY_TRANSLATION_MAP.put("одеса", "Odesa");
    CITY_TRANSLATION_MAP.put("харкові", "Kharkiv");
    CITY_TRANSLATION_MAP.put("харків", "Kharkiv");
    CITY_TRANSLATION_MAP.put("дніпрі", "Dnipro");
    CITY_TRANSLATION_MAP.put("дніпро", "Dnipro");
    CITY_TRANSLATION_MAP.put("чернівцях", "Chernivtsi");
    CITY_TRANSLATION_MAP.put("чернівці", "Chernivtsi");
    CITY_TRANSLATION_MAP.put("лондоні", "London");
    CITY_TRANSLATION_MAP.put("лондон", "London");
    CITY_TRANSLATION_MAP.put("парижі", "Paris");
    CITY_TRANSLATION_MAP.put("париж", "Paris");
  }

  public DiscordMessageListener(IntegrationService integrationService) {
    this.integrationService = integrationService;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }

    String content = event.getMessage().getContentRaw();
    String response = processMessage(content);

    if (response != null && !response.isEmpty()) {
      event.getChannel().sendMessage(response).queue();
    }
  }

  private String processMessage(String content) {
    String lowerCaseContent = content.toLowerCase().trim();

    if (lowerCaseContent.startsWith("/start")) {
      return "Вітаю! Я розумію команди `/weather [місто]`, а також фрази типу `яка погода в Києві` або `weather in London`.";
    } else if (lowerCaseContent.startsWith("/weather")) {
      String city = content.substring(8).trim();
      if (city.isEmpty()) return "Вкажіть назву міста. Приклад: `/weather Kyiv`";
      return fetchWeatherData(normalizeCity(city));
    } else {
      return processFreeText(lowerCaseContent);
    }
  }

  private String processFreeText(String content) {
    Matcher matcher = WEATHER_PATTERN.matcher(content);

    if (matcher.matches()) {
      String extractedCity = matcher.group(1).toLowerCase();
      String cityForApi = normalizeCity(extractedCity);

      logger.info("Вільний текст: вилучено '{}', відправлено до API '{}'", extractedCity, cityForApi);
      return fetchWeatherData(cityForApi);
    } else if (content.contains("привіт") || content.contains("hello")) {
      return "Привіт! Я можу надати дані про погоду. Просто запитай мене.";
    }

    return null;
  }

  private String normalizeCity(String city) {
    // Якщо місто є в карті перекладу - повертаємо англійський варіант
    // Якщо немає - повертаємо як є (на випадок якщо ввели англійською або в називному відмінку)
    return CITY_TRANSLATION_MAP.getOrDefault(city.toLowerCase(), city);
  }

  private String fetchWeatherData(String city) {
    try {
      Object weatherData = integrationService.getWeather(city);
      return "Погода для **" + city + "**:\n```json\n" + weatherData.toString() + "\n```";
    } catch (Exception e) {
      logger.error("API помилка для {}: {}", city, e.getMessage());
      return "Не вдалося знайти місто '" + city + "'. Спробуйте написати назву англійською або в називному відмінку.";
    }
  }
}
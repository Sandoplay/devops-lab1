package edu.levytskyi.lab1api.service;

import edu.levytskyi.lab1api.entity.CityTranslation;
import edu.levytskyi.lab1api.repository.CityTranslationRepository;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DiscordMessageListener extends ListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DiscordMessageListener.class);
  private final IntegrationService integrationService;
  private final CityTranslationRepository cityTranslationRepository;

  private static final Pattern WEATHER_PATTERN = Pattern.compile(
      "(?i).*(?:погод[ауі]\\s+[ву]|weather\\s+in)\\s+([а-яіїєґa-z-]+).*",
      Pattern.UNICODE_CHARACTER_CLASS
  );

  public DiscordMessageListener(IntegrationService integrationService, CityTranslationRepository cityTranslationRepository) {
    this.integrationService = integrationService;
    this.cityTranslationRepository = cityTranslationRepository;
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
      return "Доступні команди:\n" +
          "`/weather [місто]` — отримання даних про погоду.\n" +
          "`/addcity [назва1, назва2...] [english_name]` — збереження перекладу міста. Допускається введення кількох українських варіацій через кому або пробіл (приклад: `/addcity лужани лужанах luzhany`).\n" +
          "Також підтримується розпізнавання міст у вільному тексті, наприклад: `яка погода в Києві`.";
    } else if (lowerCaseContent.startsWith("/addcity")) {
      return processAddCityCommand(content);
    } else if (lowerCaseContent.startsWith("/weather")) {
      String city = content.substring(8).trim();
      if (city.isEmpty()) return "Вкажіть назву міста. Приклад: `/weather Kyiv`";
      return fetchWeatherData(normalizeCity(city));
    } else {
      return processFreeText(lowerCaseContent);
    }
  }

  private String processAddCityCommand(String content) {
    // Відкидаємо саму команду
    String args = content.substring(8).trim();

    // Знаходимо останній пробіл, щоб відділити англійську назву
    int lastSpaceIdx = args.lastIndexOf(' ');
    if (lastSpaceIdx == -1) {
      return "Неправильний формат. Використовуйте: /addcity [назва1, назва2...] [english_name]";
    }

    String eng = args.substring(lastSpaceIdx).trim();
    String ukrPart = args.substring(0, lastSpaceIdx).trim();

    // Розбиваємо українські назви по комах або пробілах
    String[] ukrVariations = ukrPart.split("[,\\s]+");

    int addedCount = 0;
    for (String ukr : ukrVariations) {
      String trimmedUkr = ukr.toLowerCase().trim();
      if (!trimmedUkr.isEmpty() && cityTranslationRepository.findByUkrainianName(trimmedUkr).isEmpty()) {
        CityTranslation translation = new CityTranslation();
        translation.setUkrainianName(trimmedUkr);
        translation.setEnglishName(eng);
        cityTranslationRepository.save(translation);
        addedCount++;
      }
    }

    return "Збережено " + addedCount + " нових варіацій назви для ідентифікатора " + eng + ".";
  }

  private String processFreeText(String content) {
    Matcher matcher = WEATHER_PATTERN.matcher(content);

    if (matcher.matches()) {
      String extractedCity = matcher.group(1).toLowerCase();
      String cityForApi = normalizeCity(extractedCity);

      logger.info("Вільний текст: вилучено '{}', відправлено до API '{}'", extractedCity, cityForApi);
      return fetchWeatherData(cityForApi);
    }
    return null;
  }

  private String normalizeCity(String city) {
    return cityTranslationRepository.findByUkrainianName(city.toLowerCase())
        .map(CityTranslation::getEnglishName)
        .orElse(city);
  }

  private String fetchWeatherData(String city) {
    try {
      Object weatherData = integrationService.getWeather(city);
      return "Погода для **" + city + "**:\n```json\n" + weatherData.toString() + "\n```";
    } catch (Exception e) {
      logger.error("API помилка для {}: {}", city, e.getMessage());
      return "Не вдалося знайти місто. Додайте переклад командою /addcity.";
    }
  }
}
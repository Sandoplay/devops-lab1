package edu.levytskyi.lab1api;

import edu.levytskyi.lab1api.entity.CityTranslation;
import edu.levytskyi.lab1api.repository.CityTranslationRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
public class Lab1ApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(Lab1ApiApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public CommandLineRunner seedData(CityTranslationRepository repository) {
    return args -> {
      if (repository.count() == 0) {
        CityTranslation kyiv = new CityTranslation();
        kyiv.setEnglishName("Kyiv");
        kyiv.setUkrainianName("Київ");

        CityTranslation london = new CityTranslation();
        london.setEnglishName("London");
        london.setUkrainianName("Лондон");

        repository.saveAll(List.of(kyiv, london));
        System.out.println(">>> Дані успішно додані в PostgreSQL!");
      }
    };
  }
}
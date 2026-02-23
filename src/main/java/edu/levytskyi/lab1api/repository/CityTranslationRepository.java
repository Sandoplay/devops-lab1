package edu.levytskyi.lab1api.repository;

import edu.levytskyi.lab1api.entity.CityTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CityTranslationRepository extends JpaRepository<CityTranslation, Long> {
  Optional<CityTranslation> findByUkrainianName(String ukrainianName);
}
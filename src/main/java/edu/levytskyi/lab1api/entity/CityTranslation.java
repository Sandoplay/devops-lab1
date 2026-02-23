package edu.levytskyi.lab1api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CityTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String ukrainianName;

  @Column(nullable = false)
  private String englishName;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUkrainianName() { return ukrainianName; }
  public void setUkrainianName(String ukrainianName) { this.ukrainianName = ukrainianName; }
  public String getEnglishName() { return englishName; }
  public void setEnglishName(String englishName) { this.englishName = englishName; }
}
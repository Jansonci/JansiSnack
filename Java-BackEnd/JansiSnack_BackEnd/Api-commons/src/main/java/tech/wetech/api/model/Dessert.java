package tech.wetech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.lang.Nullable;
import tech.wetech.api.dto.DessertDTO;

import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

/**
 * @author Jansonci
 */
@Getter
@Entity
@Table(name = "desserts")
public class Dessert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @Column
  private String category;

  @Column
  private String lifestyle;

  @Column(length = 50)
  private String flavor;

  @Column(nullable = false)
  private Double price;

  @Column(length = 30)
  private String difficulty;

  @Column(name = "prep_time")
  private Integer prepTime;

  @Column(name = "cook_time")
  private Integer cookTime;

  @Column
  private String nutritionInfo;

  @Column(length = 100)
  private String occasion;

  @Column
  private String storageInfo;

  @Column(name = "shelf_life")
  private Integer shelfLife;

  @Column(nullable = false)
  private Double rating;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(length = 50)
  private String region;

  public void setStorage(Integer storage) {
    this.storage = storage;
  }

  @Column
  private Integer storage;

  @Nullable
  @ManyToMany(fetch = LAZY, cascade = CascadeType.DETACH)
  @JoinTable(
    name = "dessert_ingredients",
    joinColumns = @JoinColumn(name = "dessert_id"),
    inverseJoinColumns = @JoinColumn(name = "ingredient_id")
  )
  private List<Ingredient> ingredients;

  public Dessert() {
  }


  @Override
  public String toString() {
    return "Dessert{" +
//      "id=" + id +
      ", name='" + name + '\'' +
      ", category='" + category + '\'' +
      ", lifestyle='" + lifestyle + '\'' +
      ", flavor='" + flavor + '\'' +
      ", price=" + price +
      ", difficulty='" + difficulty + '\'' +
      ", prepTime=" + prepTime +
      ", cookTime=" + cookTime +
      ", nutritionInfo='" + nutritionInfo + '\'' +
      ", occasion='" + occasion + '\'' +
      ", storageInfo='" + storageInfo + '\'' +
      ", shelfLife=" + shelfLife +
      ", rating=" + rating +
      ", imageUrl='" + imageUrl + '\'' +
      ", region='" + region + '\'' +
      ", ingredients=" + ingredients +
      '}';
  }

  public void setLifestyle(String lifestyle) {
    this.lifestyle = lifestyle;
  }

  public DessertDTO toDessertDTO() {
      assert ingredients != null;
      return new DessertDTO(
      id,
      name,
      category,
      lifestyle,
      flavor,
      price,
      difficulty,
      prepTime,
      cookTime,
      nutritionInfo,
      occasion,
      storageInfo,
      shelfLife,
      rating,
      imageUrl,
      region,
      ingredients.stream().map(Ingredient::getName).collect(Collectors.toList())
    );
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public void setPrepTime(Integer prepTime) {
    this.prepTime = prepTime;
  }

  public void setCookTime(Integer cookTime) {
    this.cookTime = cookTime;
  }

  public void setNutritionInfo(String nutritionInfo) {
    this.nutritionInfo = nutritionInfo;
  }

  public void setOccasion(String occasion) {
    this.occasion = occasion;
  }

  public void setStorageInfo(String storageInfo) {
    this.storageInfo = storageInfo;
  }

  public void setShelfLife(Integer shelfLife) {
    this.shelfLife = shelfLife;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public void setIngredients(@Nullable List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

}

package tech.wetech.dessert.model1;

import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

/**
 * @author Jansonci
 */
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

  public String getLifestyle() {
    return lifestyle;
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
  public String getCategory() {
    return category;
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

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }
// Constructors, getters, and setters...

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFlavor() {
    return flavor;
  }

  public Double getPrice() {
    return price;
  }

  public String getDifficulty() {
    return difficulty;
  }

  public Integer getPrepTime() {
    return prepTime;
  }

  public Integer getCookTime() {
    return cookTime;
  }

  public String getNutritionInfo() {
    return nutritionInfo;
  }

  public String getOccasion() {
    return occasion;
  }

  public String getStorageInfo() {
    return storageInfo;
  }

  public Integer getShelfLife() {
    return shelfLife;
  }

  public Double getRating() {
    return rating;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getRegion() {
    return region;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }
}

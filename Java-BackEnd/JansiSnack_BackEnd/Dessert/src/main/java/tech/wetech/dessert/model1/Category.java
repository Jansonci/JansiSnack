package tech.wetech.dessert.model1;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "categories")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @Column(length = 255)
  private String imageUrl;

  public Category() {
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addDessert(Dessert dessert) {
    dessert.setCategory(this.name);
  }
}

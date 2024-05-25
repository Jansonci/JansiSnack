package tech.wetech.dessert.model1;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * @author Jansonci
 */
@Getter
@Entity
@Table(name = "lifestyles")
public class Lifestyle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @Column()
  private String imageUrl;

  public Lifestyle() {
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void addDessert(Dessert dessert) {
    dessert.setCategory(this.name);
  }
}

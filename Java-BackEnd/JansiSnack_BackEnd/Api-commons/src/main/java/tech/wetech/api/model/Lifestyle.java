package tech.wetech.api.model;

import jakarta.persistence.*;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void addDessert(Dessert dessert) {
    dessert.setCategory(this.name);
  }
}

package model;

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

//  @OneToMany(mappedBy = "lifestyle", cascade = CascadeType.ALL)
//  private List<Dessert> desserts = new ArrayList<>();

  public Lifestyle() {
  }

  // Getters and Setters

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

//  public List<Dessert> getDesserts() {
//    return desserts;
//  }

//  public void setDesserts(List<Dessert> desserts) {
//    this.desserts = desserts;
//  }

  //   Convenience method to add a dessert to the category
  public void addDessert(Dessert dessert) {
//    this.desserts.add(dessert);
    dessert.setCategory(this.name);
  }
}

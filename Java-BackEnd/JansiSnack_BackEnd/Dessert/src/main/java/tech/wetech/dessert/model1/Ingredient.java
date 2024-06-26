package tech.wetech.dessert.model1;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

/**
 * @author Jansonci
 */
@Getter
@Entity
@Table(name = "ingredients")
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false, unique = true)
  private String name;

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDesserts(List<Dessert> desserts) {
    this.desserts = desserts;
  }

  @ManyToMany(mappedBy = "ingredients")
  private List<Dessert> desserts;
}

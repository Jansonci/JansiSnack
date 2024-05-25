package tech.wetech.order.producer.model;

import lombok.Getter;
import tech.wetech.api.dto.DessertDTO;

/**
 * @author Jansonci
 */
@Getter
public class CartDetail {
  DessertDTO dessert;
  Integer amount;

  public CartDetail() {
  }

  public void setDessert(DessertDTO dessert) {
    this.dessert = dessert;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public CartDetail(DessertDTO dessert, Integer amount) {
      this.dessert = dessert;
      this.amount = amount;
    }
}

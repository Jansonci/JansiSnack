package tech.wetech.order.producer.model;

import lombok.Data;
import tech.wetech.api.dto.DessertDTO;

/**
 * @author Jansonci
 */
@Data
public class OrderItemEntry {
  DessertDTO dessert;
  Integer quantity;

  public OrderItemEntry(DessertDTO dessert, Integer quantity) {
    this.dessert = dessert;
    this.quantity = quantity;
  }
}

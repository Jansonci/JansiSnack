package tech.wetech.order.producer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Jansonci
 */
@Data
public class OrderForCheck {
  Long orderId;

 private List<OrderItemEntry> orderItems;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  OffsetDateTime payTime;

  public OrderForCheck() {
  }

  public OrderForCheck(Long orderId, List<OrderItemEntry> orderItem, OffsetDateTime payTime, Long stillNeeds, BigDecimal amount, String address) {
    this.orderId = orderId;
    this.orderItems = orderItem;
    this.payTime = payTime;
    this.stillNeeds = stillNeeds;
    this.amount = amount;
    this.address = address;
  }

  Long stillNeeds;

  BigDecimal amount;

  String address;
}


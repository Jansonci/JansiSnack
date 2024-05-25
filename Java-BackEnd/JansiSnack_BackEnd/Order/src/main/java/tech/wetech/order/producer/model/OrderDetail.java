package tech.wetech.order.producer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * @author Jansonci
 */
@TableName(value ="order_detail")
@Data
public class OrderDetail {
  private Long orderDetailId;

  public OrderDetail() {
  }

  @Nullable
  private Long orderId;
  private Long dessertId;

  public OrderDetail(@Nullable Long orderId, Long dessertId, Integer quantity) {
    this.orderId = orderId;
    this.dessertId = dessertId;
    this.quantity = quantity;
  }

  private Integer quantity;
}

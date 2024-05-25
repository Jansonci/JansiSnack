package tech.wetech.order.producer.model;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Data;
import tech.wetech.api.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Jansonci
 * @TableName order
 */
@TableName(value ="order_info")
@Data
public class Order implements Serializable {

  private Long id;

  private Long customerId;

  @TableField(exist = false) // 标记为非数据库字段
  @Nullable
  private User customer;

  private String address;

  private Long requireTime;

  private BigDecimal totalAmount;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime orderDate;

  private State state;

  @TableField(exist = false) // 标记为非数据库字段
  private List<OrderDetail> orderDetail;

  @Serial
  private static final long serialVersionUID = 1L;

  public Order(Long customerId, String address, BigDecimal totalAmount, OffsetDateTime orderDate, State state, List<OrderDetail> orderDetail, Long requireTime) {
    this.customerId = customerId;
    this.address = address;
    this.totalAmount = totalAmount;
    this.orderDate = orderDate;
    this.state = state;
    this.orderDetail = orderDetail;
    this.requireTime = requireTime;
  }

  public Order(Long customerId, String address, OffsetDateTime orderDate, State state, BigDecimal totalAmount, Long requireTime) {
    this.customerId = customerId;
    this.address = address;
    this.orderDate = orderDate;
    this.state = state;
    this.totalAmount = totalAmount;
    this.requireTime = requireTime;
  }

  public Order() {
  }

  @Override
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if (that == null) {
      return false;
    }
    if (getClass() != that.getClass()) {
      return false;
    }
    Order other = (Order) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
      && (this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId()))
      && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
      && (this.getTotalAmount() == null ? other.getTotalAmount() == null : this.getTotalAmount().equals(other.getTotalAmount()))
      && (this.getOrderDate() == null ? other.getOrderDate() == null : this.getOrderDate().equals(other.getOrderDate()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
    result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
    result = prime * result + ((getTotalAmount() == null) ? 0 : getTotalAmount().hashCode());
    result = prime * result + ((getOrderDate() == null) ? 0 : getOrderDate().hashCode());
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", id=").append(id);
    sb.append(", customerId=").append(customerId);
    sb.append(", address=").append(address);
    sb.append(", totalAmount=").append(totalAmount);
    sb.append(", orderDate=").append(orderDate);
    sb.append(", serialVersionUID=").append(serialVersionUID);
    sb.append("]");
    return sb.toString();
  }

  public void setOrderDetails(List<OrderDetail> orderDetailsList) {
    this.orderDetail = orderDetailsList;
  }

  public enum State implements IEnum<Integer> {
    DONE(2),
    PAYED(1),
      UNPAYED(0);

    private int value;

    State(int value) {
      this.value = value;
    }

    @Override
    public Integer getValue() {
      return this.value;
    }
  }
}

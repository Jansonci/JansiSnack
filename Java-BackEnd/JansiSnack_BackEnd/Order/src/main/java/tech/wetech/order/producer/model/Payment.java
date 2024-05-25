package tech.wetech.order.producer.model;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * @TableName payment
 */
@TableName(value ="payment")
@Data
public class Payment implements Serializable {
  public Payment(Long orderId, PaymentMethod paymentMethod, BigDecimal amount, OffsetDateTime paymentDate) {
    this.orderId = orderId;
    this.paymentMethod = paymentMethod;
    this.amount = amount;
    this.paymentDate = paymentDate;
  }

  private Long id;

  public Payment() {
  }

  private Long orderId;

  @TableField(exist = false) // 标记为非数据库字段
  private Order correspondingOrder;

  private PaymentMethod paymentMethod;

  private BigDecimal amount;

  private String status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime paymentDate;

  @Serial
  private static final long serialVersionUID = 1L;

  public enum PaymentMethod implements IEnum<Integer> {
    WECHAT(0),
    ALIPAY(1),
    CREDITCARD(2);

    private int value;

    PaymentMethod(int value) {
      this.value = value;
    }

    @Override
    public Integer getValue() {
      return this.value;
    }
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
    Payment other = (Payment) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
      && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
      && (this.getPaymentMethod() == null ? other.getPaymentMethod() == null : this.getPaymentMethod().equals(other.getPaymentMethod()))
      && (this.getAmount() == null ? other.getAmount() == null : this.getAmount().equals(other.getAmount()))
      && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
      && (this.getPaymentDate() == null ? other.getPaymentDate() == null : this.getPaymentDate().equals(other.getPaymentDate()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
    result = prime * result + ((getPaymentMethod() == null) ? 0 : getPaymentMethod().hashCode());
    result = prime * result + ((getAmount() == null) ? 0 : getAmount().hashCode());
    result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
    result = prime * result + ((getPaymentDate() == null) ? 0 : getPaymentDate().hashCode());
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", id=").append(id);
    sb.append(", orderId=").append(orderId);
    sb.append(", paymentMethod=").append(paymentMethod);
    sb.append(", amount=").append(amount);
    sb.append(", status=").append(status);
    sb.append(", paymentDate=").append(paymentDate);
    sb.append(", serialVersionUID=").append(serialVersionUID);
    sb.append("]");
    return sb.toString();
  }
}

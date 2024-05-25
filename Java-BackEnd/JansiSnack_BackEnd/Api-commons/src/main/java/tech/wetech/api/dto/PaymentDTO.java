package tech.wetech.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDTO(
  Long id,
  Long orderId,
  String paymentMethod,
  BigDecimal amount,
  String status,
  LocalDateTime paymentDate
) {}

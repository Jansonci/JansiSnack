package tech.wetech.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDTO(
  Long id,
  String customerName,
  String address,
  BigDecimal totalAmount,
  LocalDateTime orderDate
) {}

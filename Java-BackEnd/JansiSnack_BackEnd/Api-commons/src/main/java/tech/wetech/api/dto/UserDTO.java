package tech.wetech.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record UserDTO(
  String username,
  String avatar,
  String gender,
  Integer liked,
  String motto,
  Integer follower,
  Integer following,
  String locale,
  Integer age,
  String job,
  List<Long> collections,
  List<String> articleCollections,
  BigDecimal balance
) {
}

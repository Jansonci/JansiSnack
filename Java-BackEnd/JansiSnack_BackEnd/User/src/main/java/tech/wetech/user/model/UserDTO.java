package tech.wetech.user.model;

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
  List<Long> collections
) {
}

package tech.wetech.api.apis;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.wetech.api.dto.UserDTO;
import tech.wetech.api.model.User;

/**
 * @author Jansonci
 */
@FeignClient(value = "user-info-service")
public interface UserFeignApi {
  @PutMapping("/users/update")
  public ResponseEntity<Void> updateUserField(@RequestBody @Valid User.UpdateUserRequest1 updateUserRequest);

  @GetMapping("/users/findUser/{userId}")
  public ResponseEntity<UserDTO> findUser(@PathVariable("userId") Long userId);
}

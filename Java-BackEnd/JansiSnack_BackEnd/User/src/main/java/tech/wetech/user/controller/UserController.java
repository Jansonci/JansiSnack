package tech.wetech.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tech.wetech.api.common.authz.RequiresPermissions;
import tech.wetech.api.dto.PageDTO;
import tech.wetech.api.dto.UserDTO;
import tech.wetech.api.model.Organization;
import tech.wetech.api.model.User;
import tech.wetech.user.service.OrganizationService;
import tech.wetech.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author Jansonci
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {

  private final OrganizationService organizationService;
  private final UserService userService;
  RestTemplate restTemplate = new RestTemplate();

  public UserController(OrganizationService organizationService, UserService userService) {
    this.organizationService = organizationService;
    this.userService = userService;
  }

  @RequiresPermissions("user:view")
  @GetMapping
  public ResponseEntity<PageDTO<User>> findUsers(Pageable pageable, User user) {
    return ResponseEntity.ok(userService.findUsers(pageable, user));
  }

  @GetMapping("/findUser/{userId}")
  public ResponseEntity<UserDTO> findUser(@PathVariable Long userId){
    return ResponseEntity.ok(userService.findUserById(userId).toUserDTo());
  }

  @PutMapping("/addArticleCollections")
  public ResponseEntity<Object> addToArticleCollections(@RequestParam Long userId, @RequestParam String articleId, @RequestParam String behavior){
    userService.addToArticleCollections(userId, articleId, behavior);
    return ResponseEntity.ok(Objects.equals(behavior, "true") ?"已添加":"已取消");
  }

  @GetMapping("/findCollections/{userId}")
  public ResponseEntity<String> findCollections(@PathVariable Long userId) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    List<String> collectionsAsString = userService.findCollections(userId).stream()
      .map(String::valueOf)
      .collect(Collectors.toList());
    String requestBody = String.format("{\"articles\": %s}", new ObjectMapper().writeValueAsString(collectionsAsString));
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
      return restTemplate.exchange("http://localhost:8080/article/collections", HttpMethod.POST, request, String.class);
  }

  @RequiresPermissions("user:create")
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) {
    Organization organization = organizationService.findOrganization(request.organizationId());
    return new ResponseEntity<>(userService.createUser(request.username(), request.avatar(), request.gender(), User.State.NORMAL, organization), HttpStatus.CREATED);
  }

  @PutMapping("/update")
  public ResponseEntity<Void> updateUserField(@RequestBody @Valid UpdateUserRequest1 updateUserRequest) {
    userService.updateUserField(updateUserRequest.userId, updateUserRequest.fieldName, updateUserRequest.fieldValue);
    return ResponseEntity.ok().build();
  }

  @RequiresPermissions("user:update")
  @PutMapping("/{userId}")
  public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserRequest request) {
    Organization organization = organizationService.findOrganization(request.organizationId());
    return ResponseEntity.ok(userService.updateUser(userId, request.avatar(), request.gender(), User.State.NORMAL, organization));
  }

  @RequiresPermissions("user:update")
  @PostMapping("/{userId}:disable")
  public ResponseEntity<User> disableUser(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.disableUser(userId));
  }

  @RequiresPermissions("user:update")
  @PostMapping("/{userId}:enable")
  public ResponseEntity<User> enableUser(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.enableUser(userId));
  }

  @RequiresPermissions("user:delete")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  record CreateUserRequest(@NotBlank String username, @NotNull User.Gender gender,
                           @NotBlank String avatar, Long organizationId) {
  }

  record UpdateUserRequest(@NotNull User.Gender gender,
                           @NotBlank String avatar, Long organizationId) {
  }

  record UpdateUserRequest1(@NotNull Long userId,
                           @NotBlank String fieldName, Object fieldValue) {
  }

}

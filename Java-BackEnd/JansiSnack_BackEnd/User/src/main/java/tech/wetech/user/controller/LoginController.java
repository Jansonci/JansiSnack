package tech.wetech.user.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.api.common.CommonResultStatus;
import tech.wetech.api.common.SecurityUtil;
import tech.wetech.api.dto.UserinfoDTO;
import tech.wetech.api.exception.UserException;
import tech.wetech.api.model.User;
import tech.wetech.api.model.UserCredential;
import tech.wetech.service.service.SessionService;
import tech.wetech.user.service.UserService;

import java.security.NoSuchAlgorithmException;

/**
 * 会话层执行逻辑：LoginController->DefaultSessionService->LocalSessionManager->SessionRepository
 * @author Jansonci
 */
@RequestMapping("/users")
@RestController
public class LoginController {
  private final UserService userService;

  private final SessionService sessionService;

  public LoginController(UserService userService, SessionService sessionService) {
    this.userService = userService;
    this.sessionService = sessionService;
  }

  @PostMapping("/login")
  private ResponseEntity<UserinfoDTO> login(@RequestBody @Valid LoginRequest request) { // @Valid会对request进行验证，只有合规的
    return ResponseEntity.ok(sessionService.login(request.username(), request.password())); // 参数才会被接受,否则抛出BAD_REQUEST异常
  }

  @PostMapping("/signUp")
  private ResponseEntity<UserinfoDTO> signUp(@RequestBody @Valid SignUpRequest signUpRequest) throws NoSuchAlgorithmException {
     if(userService.findUserByUsername(signUpRequest.username).isEmpty()) {
       User user = userService.makeUser(signUpRequest.username);
       sessionService.creatCredential(SecurityUtil.md5(signUpRequest.username,signUpRequest.password), signUpRequest.username, UserCredential.IdentityType.PASSWORD, user);
       return login(new LoginRequest(signUpRequest.username,signUpRequest.password));
     }
     else {
         throw new UserException(CommonResultStatus.ALLREADYEXIST);
     }
  }

  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    String token = request.getHeader("Authorization").replace("Bearer", "").trim();
    sessionService.logout(token);
    return ResponseEntity.ok().build();
  }

  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/userinfo")
  public ResponseEntity<UserinfoDTO> userInfo(HttpServletRequest request) {
    String token = request.getHeader("Authorization").replace("Bearer", "").trim();
    return ResponseEntity.ok(sessionService.getLoginUserInfo(token));
  }

  record LoginRequest(@NotBlank String username, @NotBlank String password) {
  }

  record SignUpRequest(@NotBlank String username, @NotBlank String password){

  }
}

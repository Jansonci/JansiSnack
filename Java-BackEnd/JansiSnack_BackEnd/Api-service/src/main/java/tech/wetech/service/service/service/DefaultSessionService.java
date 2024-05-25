package tech.wetech.service.service.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.wetech.api.common.CommonResultStatus;
import tech.wetech.api.common.Constants;
import tech.wetech.api.common.DomainEventPublisher;
import tech.wetech.api.common.SessionItemHolder;
import tech.wetech.api.dto.UserinfoDTO;
import tech.wetech.api.event.UserLoggedIn;
import tech.wetech.api.event.UserLoggedOut;
import tech.wetech.api.exception.UserException;

import tech.wetech.api.model.User;
import tech.wetech.api.model.UserCredential;
import tech.wetech.service.repository.UserCredentialRepository;
import tech.wetech.service.service.SessionService;


import java.util.UUID;

import static tech.wetech.api.model.UserCredential.IdentityType.PASSWORD;

@Service
public class DefaultSessionService implements SessionService {

  private final UserCredentialRepository userCredentialRepository;

  private final SessionManager sessionManager;

  public DefaultSessionService(UserCredentialRepository userCredentialRepository, SessionManager sessionManager) {
    this.userCredentialRepository = userCredentialRepository;
    this.sessionManager = sessionManager;
  }

  @Override
  public void creatCredential(String credential, String identifier, UserCredential.IdentityType identityType, User user){
    userCredentialRepository.save(new UserCredential(credential, identifier, identityType, user));
  }

  @Override
  public UserinfoDTO login(String username, String password) {
    UserCredential credential = userCredentialRepository.findCredential(username, PASSWORD)
      .orElseThrow(() -> new UserException(CommonResultStatus.NONEXISTENT, "用户不存在"));
    if (credential.doCredentialMatch(password)) {// 验证密码对错
      User user = credential.getUser();
      if (user.isLocked()) {
        throw new UserException(CommonResultStatus.UNAUTHORIZED, "用户已经停用，请与管理员联系");
      }
      String token = UUID.randomUUID().toString().replace("-", "");
      UserinfoDTO userinfo = new UserinfoDTO(token, user.getId(), user.getUsername(), user.getAvatar(), new UserinfoDTO.Credential(credential.getIdentifier(), credential.getIdentityType()), user.findPermissions());
      sessionManager.store(token, credential, userinfo);// 储存新会话
      SessionItemHolder.setItem(Constants.SESSION_CURRENT_USER, userinfo);
      DomainEventPublisher.instance().publish(new UserLoggedIn(userinfo, getClientIP()));// 打印事件日志
      return userinfo;
    } else {
      throw new UserException(CommonResultStatus.UNAUTHORIZED, "密码不正确");
    }
  }

  public String getClientIP() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  @Override
  public void logout(String token) {
    UserinfoDTO userinfo = (UserinfoDTO) sessionManager.get(token);
    sessionManager.invalidate(token);
    DomainEventPublisher.instance().publish(new UserLoggedOut(userinfo));
  }

  @Override
  public boolean isLogin(String token) {
    return sessionManager.get(token) != null;
  }

  @Override
  public UserinfoDTO getLoginUserInfo(String token) {
    return (UserinfoDTO) sessionManager.get(token);
  }

  @Override
  public void refresh() {
    sessionManager.refresh();
  }
}

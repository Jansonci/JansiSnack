package tech.wetech.service.infra;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.wetech.api.common.BusinessException;
import tech.wetech.api.common.CommonResultStatus;
import tech.wetech.api.common.Constants;
import tech.wetech.api.common.SessionItemHolder;
import tech.wetech.api.common.authz.PermissionHelper;
import tech.wetech.api.common.authz.RequiresPermissions;
import tech.wetech.api.dto.UserinfoDTO;
import tech.wetech.service.service.SessionService;

public class AuthInterceptor implements HandlerInterceptor {
  private final SessionService sessionService;
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 对于所有请求，都需要验证其是否已登录以及发出这个请求的用户是否拥有调用相应handler方法的许可(permissions)
   * @param sessionService
   */
  public AuthInterceptor(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (request.getHeader("Authorization") == null) {
      log.warn("Request uri {} {} is unauthorized", request.getMethod(), request.getRequestURI());
      throw new BusinessException(CommonResultStatus.UNAUTHORIZED);
    }
    String token = request.getHeader("Authorization").replace("Bearer", "").trim();
    if (!sessionService.isLogin(token)) {
      throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "未登录");
    }
    UserinfoDTO loginUserInfo = sessionService.getLoginUserInfo(token);
    if (handler instanceof HandlerMethod handlerMethod) {
      RequiresPermissions requiresPermissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
      if (requiresPermissions != null) {
        if (!PermissionHelper.isPermitted(loginUserInfo.permissions(), requiresPermissions.value(), requiresPermissions.logical())) {
          throw new BusinessException(CommonResultStatus.FORBIDDEN);
        }
      }
    }
    SessionItemHolder.setItem(Constants.SESSION_CURRENT_USER, loginUserInfo);
    return true;
  }
}

package tech.wetech.service.service.service;
import tech.wetech.api.model.UserCredential;

import java.io.Serializable;

/**
 * @author Jansonci
 */
public interface SessionManager {
  /**
   * 储存或刷新一个特定的会话
   * @param key
   * @param credential
   * @param value
   */
  void store(String key, UserCredential credential, Serializable value);

  /**
   * 根据token取消对应会话权限
   * @param key
   */
  void invalidate(String key);

  /**
   * 根据token获取当前登录用户
   * @param key
   * @return
   */
  Object get(String key);
/**
  刷新会话库存中的所有会话
 */
  void refresh();
}

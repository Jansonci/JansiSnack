package tech.wetech.api.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jansonci
 */
public class SessionItemHolder {

  private SessionItemHolder() {
  }

  private static final ThreadLocal<Map<String, Object>> store = InheritableThreadLocal.withInitial(HashMap::new);

  /**
   * 设置正在使用线程当前的用户
   * @param key
   * @param obj
   */
  public static void setItem(String key, Object obj) {
    store.get().put(key, obj);
  }

  /**
   * 获取正在使用当前线程的用户
   * @param key
   * @return
   */
  public static final Object getItem(String key) {
    return store.get().get(key);
  }

  public static final void clear() {
    store.remove();
  }

}

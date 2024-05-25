package tech.wetech.api.common;

/**
 * @author Jansonci
 */
public interface ResultStatus {
  /**
   * 错误码
   */
  int getCode();

  /**
   * 错误信息
   */
  String getMessage();
}

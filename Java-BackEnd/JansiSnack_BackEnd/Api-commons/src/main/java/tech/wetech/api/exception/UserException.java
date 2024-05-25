package tech.wetech.api.exception;

import tech.wetech.api.common.BusinessException;
import tech.wetech.api.common.ResultStatus;

/**
 * @author cjbi
 */
public class UserException extends BusinessException {

  public UserException(ResultStatus status) {
    super(status);
  }

  public UserException(ResultStatus status, String message) {
    super(status, message);
  }
}

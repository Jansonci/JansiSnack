package tech.wetech.api.exception;


import tech.wetech.api.common.BusinessException;
import tech.wetech.api.common.ResultStatus;

/**
 * @author cjbi
 */
public class StorageException extends BusinessException {

  public StorageException(ResultStatus status) {
    super(status);
  }

  public StorageException(ResultStatus status, String message) {
    super(status, message);
  }
}

package tech.wetech.api.common;


import lombok.Getter;

/**
 * @author Jansonci
 */
@Getter
public class BusinessException extends RuntimeException {
  private final ResultStatus status;

  public BusinessException(ResultStatus status) {
    super(status.getMessage());
    this.status = status;
  }

  public BusinessException(ResultStatus status, String message) {
    super(message);
    this.status = status;
  }
}

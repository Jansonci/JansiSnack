package tech.wetech.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import tech.wetech.api.common.BusinessException;
import tech.wetech.api.common.CommonResultStatus;
import tech.wetech.api.common.SecurityUtil;
import tech.wetech.api.model.BaseEntity;


import java.security.NoSuchAlgorithmException;

/**
 * 用户凭证
 *
 * @author cjbi
 */
@Entity
public class UserCredential extends BaseEntity {

  /**
   * 标识（手机号 邮箱 用户名或第三方应用的唯一标识）
   */
  @Column(nullable = false)
  private String identifier;
  /**
   * 密码凭证（站内的保存密码，站外的不保存或保存token）
   */
  @Column(nullable = false)
  private String credential;
  /**
   * 登录类型（手机号 邮箱 用户名）或第三方应用名称（微信 微博等）
   */
  private IdentityType identityType;

  @OneToOne
  private User user;

  public UserCredential(String credential,String identifier, IdentityType identityType, User user) {
    this.identifier = identifier;
    this.credential = credential;
    this.identityType = identityType;
    this.user = user;
  }

  public UserCredential() {
  }

  public boolean doCredentialMatch(String password) {
    try {
      //TODO 未实现其他登录方式
      if (this.getIdentityType() != IdentityType.PASSWORD || !SecurityUtil.md5(identifier, password).equals(this.getCredential())) {
        return false;
      }
    } catch (NoSuchAlgorithmException e) {
      throw new BusinessException(CommonResultStatus.FAIL, "密码加密失败：" + e.getMessage());
    }
    return true;
  }

  public enum IdentityType {
    PASSWORD,
    EMAIL,
    USERNAME

  }

  public String getIdentifier() {
    return identifier;
  }

  public String getCredential() {
    return credential;
  }

  public IdentityType getIdentityType() {
    return identityType;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setCredential(String credential) {
    this.credential = credential;
  }

  public void setIdentityType(IdentityType identityType) {
    this.identityType = identityType;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
}

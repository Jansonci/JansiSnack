package tech.wetech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import tech.wetech.api.common.JsonUtils;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话
 *
 * @author Jansonci
 */
@Getter
@Entity
public class Session extends BaseEntity {

  @Column(nullable = false)
  private String token;
  @OneToOne
  private UserCredential credential;
  @Column(nullable = false)
  private LocalDateTime expireTime;
  @Column(nullable = false)
  private LocalDateTime lastLoginTime;

  private LocalDateTime lastModifiedTime;

  @Lob
  @Column(length = Integer.MAX_VALUE)
  private String data;

  @Transient // 不会出现在表中
  private boolean active;


  public static Session of(Long id, String token, UserCredential credential, Serializable data, LocalDateTime expireTime) {
    Session authSession = new Session();
    authSession.setId(id);
    authSession.setToken(token);
    authSession.setCredential(credential);
    authSession.setExpireTime(expireTime);
    authSession.setLastLoginTime(LocalDateTime.now());
    authSession.setActive(true);
    authSession.setData(JsonUtils.stringify(data));
    return authSession;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setCredential(UserCredential credential) {
    this.credential = credential;
  }

  public void setExpireTime(LocalDateTime expireTime) {
    this.expireTime = expireTime;
  }

  public void setLastLoginTime(LocalDateTime lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}

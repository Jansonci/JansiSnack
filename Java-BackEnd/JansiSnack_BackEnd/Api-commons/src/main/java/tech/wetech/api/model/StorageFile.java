package tech.wetech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import tech.wetech.api.common.Constants;
import tech.wetech.api.common.SessionItemHolder;
import tech.wetech.api.dto.UserinfoDTO;


import java.time.LocalDateTime;

/**
 * @author Jansonci
 */
@Getter
@Entity
@Table(name = "storage_file",uniqueConstraints = {@UniqueConstraint(columnNames = {"key"})})// 将key属性设定为唯一约束，即所有属性都可以由key单独推出
public class StorageFile extends BaseEntity {

  /**
   * 文件的唯一索引
   */
  @Column(name = "`key`")
  private String key;

  /**
   * 文件名
   */
  private String name;

  /**
   * 文件类型
   */
  private String type;

  /**
   * 文件大小
   */
  private Long size;

  /**
   * 文件访问链接
   */
  private String url;

  private String createUser;

  private LocalDateTime createTime;

  private String storageId;

  @PrePersist
  protected void onCreate() {
    createTime = LocalDateTime.now();
    UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
    createUser = userInfo.username();
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public void setStorageId(String storageId) {
    this.storageId = storageId;
  }
}

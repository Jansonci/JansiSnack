package tech.wetech.api.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.DETACH;

/**
 * 角色
 *
 * @author Jansonci
 */
@Getter
@Entity
public class Role extends BaseEntity {

  @Column(unique = true, nullable = false) // unique表示同一列中name属性能否重复，nullable表示可否为空
  private String name;

  private String description;

  private Boolean available = Boolean.FALSE;

  @ManyToMany(fetch = FetchType.LAZY, cascade = DETACH)
  @JoinTable(name = "role_resource", // 为Resource和Role建立中间表
    joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), // 指出从主类实体(Role)选出哪些属性作为中间表的属性
    inverseJoinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id")) // 指出从客类实体(Resource)选出哪些属性作为中间表的属性
  private Set<Resource> resources = new LinkedHashSet<>();

  @ManyToMany(fetch = FetchType.LAZY, cascade = DETACH)
  @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
  private Set<User> users = new LinkedHashSet<>();

  public Role() {

  }


  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public void setResources(Set<Resource> resources) {
    this.resources = resources;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}

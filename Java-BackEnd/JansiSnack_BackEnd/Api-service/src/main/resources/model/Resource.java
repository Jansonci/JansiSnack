package model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 资源
 *
 * @author cjbi
 */
@Entity
public class Resource extends BaseEntity {

  private String name;

  private Type type;

  private String permission;

  @ManyToOne(fetch = FetchType.LAZY)// 由于这里的多对一是同一实体的内部映射，所以不需要再使用JoinColumn来创建外键
  private Resource parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)// mappedBy表示放弃对关系的维护，即不创建中间表
  private Set<Resource> children = new LinkedHashSet<>();

  private String parentIds; //父编号列表

  private String icon;
  private String url;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinTable(name = "role_resource", // 为Resource和Role建立中间表
    joinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id"), // 指出从主类实体(Resource)选出哪些属性作为中间表的属性
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")) // 指出从客类实体(Role)选出哪些属性作为中间表的属性
  private Set<Role> roles = new LinkedHashSet<>();

  public enum Type {
    MENU, BUTTON
  }

  public String getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Resource getParent() {
    return parent;
  }

  public void setParent(Resource parent) {
    this.parent = parent;
  }

  public Set<Resource> getChildren() {
    return children;
  }

  public String getParentIds() {
    return parentIds;
  }

  public void setParentIds(String parentIds) {
    this.parentIds = parentIds;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setChildren(Set<Resource> children) {
    this.children = children;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}

package model;

import jakarta.persistence.*;
import tech.wetech.api.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 用户
 *
 * @author cjbi
 */
@Entity
public class User extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String username;

  private String avatar;

  @Column
  private String motto;

  @Column
  private Integer liked;

  @Column
  private Integer follower;

  @Column
  private Integer following;

  @Column
  private Gender gender;

  @Column
  private String locale;

  @Column
  private Integer age;

  @Column
  private String job;

  @Column(nullable = false)
  private State state;

  @ManyToMany
  @JoinTable(
    name = "user_dessert", // 定义中间表名
    joinColumns = @JoinColumn(name = "user_id"), // 定义中间表的列名，对应User的ID
    inverseJoinColumns = @JoinColumn(name = "dessert_id") // 定义中间表的列名，对应Dessert的ID
  )
  private List<Dessert> collections;

  @Column
  private LocalDateTime createdTime;

  @ManyToMany(fetch = LAZY, cascade = CascadeType.DETACH)
  @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Set<Role> roles = new LinkedHashSet<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", orphanRemoval = true)// 通常OneToMany会加上JoinTable注解来生成中间表，但在
  private Set<UserCredential> credentials = new LinkedHashSet<>();//另一方（UserCredential）已经使用JoinColumn注解生成外键来维护关系以后
  // 便没有必要再生成中间表，而是使用mappedBy指明User对应于UserCredential表中的哪一列（外键）
  @OneToOne(fetch = LAZY)
  private Organization organization;

  public String getOrgFullName() {
    return concatOrgName(getOrganization());
  }

  private String concatOrgName(Organization org) {
    if (org.getParent() != null) {
      return concatOrgName(org.getParent()).concat("-").concat(org.getName());
    }
    return org.getName();
  }

  @PrePersist
  protected void onCreate() {
    this.createdTime = LocalDateTime.now();
  }

  /**
   * 获取用户权限列表
   *
   * @return
   */
  public Set<String> findPermissions() {
    return roles.stream()
      .map(Role::getResources)
      .flatMap(Collection::stream)
      .map(Resource::getPermission)
      .collect(Collectors.toSet());
  }

  public enum Gender {
    MALE, FEMALE
  }

  public enum State {
    NORMAL, LOCKED
  }

  public UserDTO toUserDTo() {
    return new UserDTO(
      username,
      avatar,
      gender== Gender.MALE?"男":"女",
      liked,
      motto,
      follower,
      following,
      locale,
      age,
      job,
      collections.stream().map(Dessert::getId).toList(),
      );
  }
  public boolean isLocked() {
    return this.state == State.LOCKED;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public Set<UserCredential> getCredentials() {
    return credentials;
  }

  public void setCredentials(Set<UserCredential> credentials) {
    this.credentials = credentials;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }
}

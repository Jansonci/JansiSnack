package tech.wetech.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import tech.wetech.api.dto.UserDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 用户
 *
 * @author Jansonci
 */
@Getter
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

  @Getter
  @Column
  private Gender gender;

  @Column
  private String locale;

  @Column
  private Integer age;

  @Column
  private String job;

  @Getter
  @Column
  private State state;

  @Column
  private BigDecimal balance = BigDecimal.valueOf(0.00);

  @ManyToMany
  @JoinTable(
    name = "user_dessert", // 定义中间表名
    joinColumns = @JoinColumn(name = "user_id"), // 定义中间表的列名，对应User的ID
    inverseJoinColumns = @JoinColumn(name = "dessert_id") // 定义中间表的列名，对应Dessert的ID
  )
  private List<Dessert> collections;

  @Getter
  @ElementCollection
  @CollectionTable(name = "user_article", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "article_id")
  private List<String> articleCollections;

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

  public User() {
  }

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
      gender == null?null:(gender== Gender.MALE?"男":"女"),
      liked,
      motto,
      follower,
      following,
      locale,
      age,
      job,
      collections.stream().map(Dessert::getId).toList(),
      articleCollections,
      balance
      );
  }
  public boolean isLocked() {
    return this.state == State.LOCKED;
  }


  public void setUsername(String username) {
    this.username = username;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void setCredentials(Set<UserCredential> credentials) {
    this.credentials = credentials;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public void setArticleCollections(List<String> articleCollections) {
    this.articleCollections = articleCollections;
  }

  public record UpdateUserRequest1(@NotNull Long userId,
                            @NotBlank String fieldName, Object fieldValue) {
  }
}

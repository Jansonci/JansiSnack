package tech.wetech.service.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.api.common.BusinessException;
import tech.wetech.api.common.CommonResultStatus;
import tech.wetech.api.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements CustomUserRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void updateUserField(Long id, String fieldName, Object fieldValue) {
    // 使用 Criteria API 或原生 SQL 语句来实现更新
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> update = cb.createCriteriaUpdate(User.class);
    Root<User> root = update.from(User.class);
    if (Objects.equals(fieldName, "balance")) fieldValue = BigDecimal.valueOf((Double) fieldValue);
    update.set(root.get(fieldName), fieldValue);

    update.where(cb.equal(root.get("id"), id));
    entityManager.createQuery(update).executeUpdate();
  }

  @Transactional
  public void addToArticleCollections(Long userId, String articleId, String behavior) {
    User user = entityManager.find(User.class, userId);
    if (user == null) {
      throw new BusinessException(CommonResultStatus.NONEXISTENT);
    }

  if(Objects.equals(behavior, "true")){
    try {
      entityManager.createNativeQuery("SELECT * FROM user_article WHERE user_id = :userId AND article_id = :articleId")
        .setParameter("userId", userId)
        .setParameter("articleId", articleId)
        .getSingleResult();
      // 如果查询没有抛出异常，说明记录已存在
      throw new BusinessException(CommonResultStatus.COLLECTIONALREADYEXISTS);
    } catch (NoResultException e) {
      // 记录不存在，进行插入
      entityManager.createNativeQuery("INSERT INTO user_article (user_id, article_id) VALUES (:userId, :articleId)")
        .setParameter("userId", userId)
        .setParameter("articleId", articleId)
        .executeUpdate();
    }
  }
  else {
    // 如果behavior不是"true"，执行删除操作
        int affectedRows = entityManager.createNativeQuery("DELETE FROM user_article WHERE user_id = :userId AND article_id = :articleId")
          .setParameter("userId", userId)
          .setParameter("articleId", articleId)
          .executeUpdate();
        if (affectedRows == 0) {
          // 如果没有行受影响，说明记录不存在
          throw new BusinessException(CommonResultStatus.RECORD_NOT_EXIST);
        }
     }
  }

  public List<String> findCollections(Long userId) {
    // 使用原生SQL查询用户的所有收藏文章
    String sql = "SELECT article_id FROM user_article WHERE user_id = :userId";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("userId", userId);

    // 执行查询并返回结果
    return query.getResultList();
  }
}

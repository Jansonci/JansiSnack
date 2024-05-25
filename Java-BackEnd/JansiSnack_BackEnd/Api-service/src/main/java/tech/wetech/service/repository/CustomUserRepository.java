package tech.wetech.service.repository;

import java.util.List;

public interface CustomUserRepository {
  void updateUserField(Long id, String fieldName, Object fieldValue);

  void addToArticleCollections(Long userId, String articleId,String behavior);

  List<String> findCollections(Long userId);
}

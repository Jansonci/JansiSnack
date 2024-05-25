package tech.wetech.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.api.model.StorageFile;

/**
 * @author cjbi
 */
@Repository
public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {

  @Query("from StorageFile where key=:key")
  StorageFile getByKey(String key);

  @Modifying
  @Query("from StorageFile where key=:key")
  void deleteByKey(String key);
}

package tech.wetech.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.api.model.StorageConfig;

/**
 * @author cjbi
 */
@Repository
public interface StorageConfigRepository extends JpaRepository<StorageConfig, Long> {

  @Query("from StorageConfig where isDefault=true")
  StorageConfig getDefaultConfig();

  StorageConfig getByStorageId(String storageId);

}

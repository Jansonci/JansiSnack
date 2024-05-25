package tech.wetech.service.service;
// 对象存储

import org.springframework.core.io.Resource;
import tech.wetech.api.model.StorageConfig;
import tech.wetech.api.model.StorageFile;

import java.io.InputStream;
import java.util.List;
/**
 * @author Jansonci
 */
public interface StorageService {

  List<StorageConfig> findConfigList();

  StorageConfig getConfig(Long id);

  StorageConfig createConfig(String name, StorageConfig.Type type, String endpoint, String accessKey, String secretKey, String bucketName, String address, String storagePath);

  StorageConfig updateConfig(Long id, String name, StorageConfig.Type type, String endpoint, String bucketName, String accessKey, String secretKey, String address, String storagePath);

  void deleteConfig(StorageConfig storageConfig);

  void markAsDefault(StorageConfig storageConfig);

  String store(String storageId, InputStream inputStream, long contentLength, String contentType, String filename);

  void delete(String key);

  StorageFile getByKey(String key);

  Resource loadAsResource(String key);
}

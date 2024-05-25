package tech.wetech.service.service.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.api.common.DomainEventPublisher;
import tech.wetech.api.common.NanoId;
import tech.wetech.api.common.StringUtils;
import tech.wetech.api.event.StorageConfigCreated;
import tech.wetech.api.event.StorageConfigDeleted;
import tech.wetech.api.event.StorageConfigMarkedAsDefault;
import tech.wetech.api.event.StorageConfigUpdated;
import tech.wetech.api.exception.StorageException;
import tech.wetech.api.model.StorageConfig;
import tech.wetech.api.model.StorageFile;
import tech.wetech.service.infra.storage.LocalStorage;
import tech.wetech.service.infra.storage.S3Storage;
import tech.wetech.service.infra.storage.Storage;
import tech.wetech.service.repository.StorageConfigRepository;
import tech.wetech.service.repository.StorageFileRepository;
import tech.wetech.service.service.StorageService;


import java.io.InputStream;
import java.util.List;

import static tech.wetech.api.common.CommonResultStatus.FAIL;

/**
 * @author Jansonci
 */ // 对于Resource而言存在两个不同的储存体系：S3Storage和StorageFileRepository，而对于config而言只有StorageConfigRepository，这是由对象
// 体积所决定的。S3Storage储存资源的具体内容，而StorageFileRepository只储存文件的基本信息
@Service
public class StorageServiceImpl implements StorageService {

  private final StorageConfigRepository storageConfigRepository;
  private final StorageFileRepository storageFileRepository;

  public StorageServiceImpl(StorageConfigRepository storageConfigRepository, StorageFileRepository storageFileRepository) {
    this.storageConfigRepository = storageConfigRepository;
    this.storageFileRepository = storageFileRepository;
  }

  @Override
  public List<StorageConfig> findConfigList() {
    return storageConfigRepository.findAll();
  }

  @Override
  public StorageConfig getConfig(Long id) {
    return storageConfigRepository.findById(id)
      .orElseThrow(() -> new StorageException(FAIL, "存储配置不存在"));
  }

  @Override
  @Transactional
  public StorageConfig createConfig(String name, StorageConfig.Type type, String endpoint, String accessKey, String secretKey, String bucketName, String address, String storagePath) {
    StorageConfig config = storageConfigRepository.save(buildConfig(null, name, type, endpoint, bucketName, accessKey, secretKey, address, storagePath));
    DomainEventPublisher.instance().publish(new StorageConfigCreated(config));
    return config;
  }

  private StorageConfig buildConfig(Long id, String name, StorageConfig.Type type, String endpoint, String bucketName, String accessKey, String secretKey, String address, String storagePath) {
    StorageConfig storageConfig;
    if (id != null) {
      storageConfig = getConfig(id);
      storageConfig.setId(id);
    } else {
      storageConfig = new StorageConfig();
      storageConfig.setStorageId(NanoId.randomNanoId());//????
    }
    storageConfig.setName(name);
    storageConfig.setType(type);
    storageConfig.setAccessKey(accessKey);
    storageConfig.setSecretKey(secretKey);
    storageConfig.setEndpoint(endpoint);
    storageConfig.setBucketName(bucketName);
    storageConfig.setAddress(address);
    storageConfig.setStoragePath(storagePath);
    return storageConfig;
  }

  @Override
  @Transactional
  public StorageConfig updateConfig(Long id, String name, StorageConfig.Type type, String endpoint, String accessKey, String secretKey, String bucketName, String address, String storagePath) {
    StorageConfig config = storageConfigRepository.save(buildConfig(id, name, type, endpoint, bucketName, accessKey, secretKey, address, storagePath));
    DomainEventPublisher.instance().publish(new StorageConfigUpdated(config));
    return config;
  }

  @Override
  @Transactional
  public void deleteConfig(StorageConfig storageConfig) {
    if (storageConfig.isDefault()) {
      throw new StorageException(FAIL, "不能删除默认配置");
    }
    storageConfigRepository.delete(storageConfig);
    DomainEventPublisher.instance().publish(new StorageConfigDeleted(storageConfig));
  }

  @Override
  @Transactional
  public void markAsDefault(StorageConfig storageConfig) {
    storageConfig.setIsDefault(true);
    List<StorageConfig> configList = findConfigList();
    for (StorageConfig record : configList) {
      record.setIsDefault(record.equals(storageConfig));
    }
    storageConfigRepository.saveAll(configList);
    DomainEventPublisher.instance().publish(new StorageConfigMarkedAsDefault(storageConfig));
  }


  @Override
  @Transactional
  public String store(String storageId, InputStream inputStream, long contentLength, String contentType, String filename) {
    String key = generateKey(filename);
    Storage storage;
    if (storageId != null) {
      storage = getStorage(storageId);
    } else {
      storage = getStorage();
    }
    storage.store(inputStream, contentLength, contentType, key);// 向S3Storage中存放
    String url = getStorage().getUrl(key);
    StorageFile storageFile = new StorageFile();
    storageFile.setKey(key);
    storageFile.setName(filename);
    storageFile.setType(contentType);
    storageFile.setSize(contentLength);
    storageFile.setUrl(url);
    storageFile.setStorageId(storage.getId());
    storageFileRepository.save(storageFile); // 向storageFileRepository中存放
    return url;
  }

// 获取默认S3Storage对象
  private Storage getStorage() {
    StorageConfig config = storageConfigRepository.getDefaultConfig();
    if (config.getType() == StorageConfig.Type.LOCAL) {
      return new LocalStorage(config);
    }
    return new S3Storage(config);
//    return new LocalStorage(config);
  }

  // 获取特定S3Storage对象
  private Storage getStorage(String storageId) {
    StorageConfig config = storageConfigRepository.getByStorageId(storageId);
    if (config.getType() == StorageConfig.Type.LOCAL) {
      return new LocalStorage(config);
    }
    return new S3Storage(config);
    //    return new LocalStorage(config);
  }

  private String generateKey(String filename) {
    int index = filename.lastIndexOf('.');
    String suffix = filename.substring(index);

    String key = null;
    StorageFile storageFile = null;

    do {
      key = StringUtils.getRandomString(20) + suffix;
      storageFile = storageFileRepository.getByKey(key);
    } while (storageFile != null);
    return key;
  }

  @Override
  public void delete(String key) {
    StorageFile storageFile = storageFileRepository.getByKey(key);
    getStorage(storageFile.getStorageId()).delete(key);
    storageFileRepository.delete(storageFile);
  }

  @Override
  public StorageFile getByKey(String key) {
    return storageFileRepository.getByKey(key);
  }

  @Override
  public Resource loadAsResource(String key) {
    StorageFile file = storageFileRepository.getByKey(key);// 先从storageFileRepository中获得file的基本信息
    Storage storage = getStorage(file.getStorageId());// 从基本信息中获取储存file的S3Storage对象的id在根据此id获得S3Storage对象
    return storage.loadAsResource(key);// 最后从特定S3Storage对象中查到具体的file内容
  }

}

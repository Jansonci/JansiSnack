package tech.wetech.service.infra.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import tech.wetech.api.model.StorageConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author cjbi
 * 此storage类和S3storage类似，但一个面向本地储存，一个面向云储存
 */
public class LocalStorage implements Storage {

  private final StorageConfig config;

  private final Path rootLocation;

  public LocalStorage(StorageConfig config) {
    this.config = config;

    this.rootLocation = Paths.get(config.getStoragePathWithEnv());
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String getId() {
    return config.getStorageId();
  }

  @Override
  public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
    try {
      Files.copy(inputStream, rootLocation.resolve(keyName), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store file " + keyName, e);
    }
  }

  private Path get(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      Path file = get(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        return null;
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void delete(String filename) {
    Path file = get(filename);
    try {
      Files.delete(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getUrl(String keyName) {
    return config.getAddressWithEnv() + keyName;
  }
}

package tech.wetech.api;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jansonci
 */
@Configuration
@EnableCaching
public class CacheConfig {
  @Bean
  public CacheManager cacheManager() {
    // 使用ConcurrentMapCacheManager作为简单的缓存演示
    return new ConcurrentMapCacheManager("dessert", "desserts");
  }
}

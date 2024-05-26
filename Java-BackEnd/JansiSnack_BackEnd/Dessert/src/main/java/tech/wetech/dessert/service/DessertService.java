package tech.wetech.dessert.service;

import io.seata.spring.annotation.GlobalTransactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.api.common.BusinessException;
import tech.wetech.api.dto.DessertDTO;
import tech.wetech.api.model.Category;
import tech.wetech.api.model.Dessert;
import tech.wetech.api.model.Lifestyle;
import tech.wetech.dessert.repository.CategoryRepository;
import tech.wetech.dessert.repository.DessertRepository;
import tech.wetech.dessert.repository.LifestyleRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static tech.wetech.api.common.CommonResultStatus.RECORD_NOT_EXIST;


/**
 * @author Jansonci
 */
@Service
public class DessertService {
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public final DessertRepository dessertRepository;

  private static final String DESSERT_CACHE_PREFIX = "dessert:";
  private static final String CATEGORY_CACHE_KEY = "categories";
  private static final String LIFESTYLE_CACHE_KEY = "lifestyles";
  private static final String DESSERT_STORAGE_CACHE_PREFIX = "dessert:storage:";

  public final CategoryRepository categoryRepository;
  public final LifestyleRepository lifestyleRepository;

  public DessertService(DessertRepository dessertRepository, CategoryRepository categoryRepository, LifestyleRepository lifestyleRepository) {
    this.dessertRepository = dessertRepository;
    this.categoryRepository = categoryRepository;
    this.lifestyleRepository = lifestyleRepository;
  }

  public DessertDTO findDessertByNameWithRedis(Long name) {
    // 从Redis缓存中获取数据
    String cacheKey = DESSERT_CACHE_PREFIX + name;
    DessertDTO dessertDTO = (DessertDTO) redisTemplate.opsForValue().get(cacheKey);
    if (dessertDTO == null) {
      // 如果缓存中没有数据，则从数据库中获取
      Dessert dessert = dessertRepository.findById(name)
        .orElseThrow(() -> new BusinessException(RECORD_NOT_EXIST));
      dessertDTO = dessert.toDessertDTO();
      // 将数据存储到Redis缓存中，设置缓存时间为1小时
      redisTemplate.opsForValue().set(cacheKey, dessertDTO, 1, TimeUnit.HOURS);
    }
    return dessertDTO;
  }

  public List<DessertDTO> findAllDesserts() {
    String cacheKey = DESSERT_CACHE_PREFIX + "all";
    List<DessertDTO> desserts = (List<DessertDTO>) redisTemplate.opsForValue().get(cacheKey);

    if (desserts == null) {
      desserts = dessertRepository.findAll().stream()
        .map(Dessert::toDessertDTO)
        .collect(Collectors.toList());
      redisTemplate.opsForValue().set(cacheKey, desserts, 1, TimeUnit.HOURS);
    }
    return desserts;
  }

  public Set<DessertDTO> findSomeDesserts(Set<Long> dessertIds) {
      return dessertRepository.findAllById(dessertIds).stream()
        .map(Dessert::toDessertDTO)
        .collect(Collectors.toSet());
  }

  public List<Category> findAllCategories() {
    List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);
    if (categories == null) {
      categories = categoryRepository.findAll();
      redisTemplate.opsForValue().set(CATEGORY_CACHE_KEY, categories, 1, TimeUnit.HOURS);
    }
    return categories;
  }

  public List<Lifestyle> findAllLifestyles() {
    List<Lifestyle> lifestyles = (List<Lifestyle>) redisTemplate.opsForValue().get(LIFESTYLE_CACHE_KEY);
    if (lifestyles == null) {
      lifestyles = lifestyleRepository.findAll();
      redisTemplate.opsForValue().set(LIFESTYLE_CACHE_KEY, lifestyles, 1, TimeUnit.HOURS);
    }
    return lifestyles;
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateDessertsStorage(Map<Long, Integer> orderInfo ) {
    orderInfo.forEach((dessertId, quantity) -> {
      String cacheKey = DESSERT_CACHE_PREFIX + dessertId;
      Dessert dessert = entityManager.find(Dessert.class, dessertId);
      if (dessert != null && dessert.getStorage() >= quantity) {
        // 更新存储量
        dessert.setStorage(dessert.getStorage() - quantity);
        entityManager.merge(dessert);
      } else {
        // 可以抛出一个自定义的异常或者处理存储不足的情况
        throw new RuntimeException("Storage is not enough for dessert id: " + dessertId);
      }
      redisTemplate.opsForValue().set(cacheKey, dessert.toDessertDTO(), 1, TimeUnit.HOURS);
    });
    entityManager.flush(); // 确保所有更新都被提交
  }
}

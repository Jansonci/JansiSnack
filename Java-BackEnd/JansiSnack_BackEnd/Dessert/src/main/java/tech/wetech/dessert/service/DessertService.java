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

  public final CategoryRepository categoryRepository;
  public final LifestyleRepository lifestyleRepository;

  public DessertService(DessertRepository dessertRepository, CategoryRepository categoryRepository, LifestyleRepository lifestyleRepository) {
    this.dessertRepository = dessertRepository;
    this.categoryRepository = categoryRepository;
    this.lifestyleRepository = lifestyleRepository;
  }

  public DessertDTO findDessertByName(Long name){
    Dessert d = dessertRepository.findById(name).orElseThrow(() -> new BusinessException(RECORD_NOT_EXIST));
    return d.toDessertDTO();
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
    return dessertRepository.findAll().stream().map(Dessert::toDessertDTO).toList();
  }

  public Set<DessertDTO> findSomeDesserts(Set<Long> dessertIds) {
      return dessertRepository.findAllById(dessertIds).stream().map(Dessert::toDessertDTO).collect(Collectors.toSet());
  }

  public List<Category> findAllCategories() {
    return categoryRepository.findAll();
  }

  public List<Lifestyle> findAllLifestyles() {
    return lifestyleRepository.findAll();
  }

  @Transactional
  public void updateDessertsStorage(Map<Long, Integer> orderInfo ){
    for (Map.Entry<Long, Integer> entry : orderInfo.entrySet()) {
      Long dessertId = entry.getKey();
      Integer quantity = entry.getValue();

      // 查找对应的甜品记录
      Dessert dessert = entityManager.find(Dessert.class, dessertId);
      if (dessert != null && dessert.getStorage() >= quantity) {
        // 更新存储量
        dessert.setStorage(dessert.getStorage() - quantity);
        entityManager.merge(dessert);
      } else {
        // 可以抛出一个自定义的异常或者处理存储不足的情况
        throw new RuntimeException("Storage is not enough for dessert id: " + dessertId);
      }
    }
    entityManager.flush(); // 确保所有更新都被提交
  }
}

package tech.wetech.order.producer.service;

import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tech.wetech.api.apis.PayFeignApi;
import tech.wetech.order.producer.mylock.DistributedLockFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * @author Jansonci
 */
@Service
public class CartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DistributedLockFactory distributedLockFactory;
    @Resource
    private PayFeignApi payFeignApi;

    public Map<Long, Integer> getCart(Long userId) {
      // 获取Redis中哈希操作的句柄
      HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

      // 为用户购物车构建正确的Redis键
      String cartKey = "cart:" + userId;

      // 从Redis获取用户购物车的所有项
      Map<String, String> rawCart = hashOps.entries(cartKey);

      // 将结果转换为 Long 和 Integer 的映射
      Map<Long, Integer> cart = new HashMap<>();
      for (Map.Entry<String, String> entry : rawCart.entrySet()) {
        cart.put(Long.parseLong(entry.getKey()), Integer.parseInt(entry.getValue()));
      }

      return cart;
    }

//    @Transactional
    @GlobalTransactional(name = "zzyy-create-order",rollbackFor = Exception.class) //AT
    public boolean updateCartRequest(Long userId, Long dessertId, Integer operation){
      Lock redisLock = distributedLockFactory.getDistributedLock("redis");
      redisLock.lock();
      try
      {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        // 构建Redis键
        String cartKey = "cart:" + userId;

        // 构建甜品ID键
        String dessertKey = dessertId.toString();

        int currentQuantity = hashOps.get(cartKey, dessertKey) == null ? 0 : Integer.parseInt(Objects.requireNonNull(hashOps.get(cartKey, dessertKey)));

        // 根据operation决定增加或减少的数量, 0表示加一，1表示减一，2表示直接减到零
        int increment;
        if (operation == -1) {
          // 直接减到零，相当于设置数量为零
          increment = -currentQuantity;  // 这将当前数量设置为0
        } else // 数量加一
            // 其他正整数，增加对应数量
            // 直接使用operation的值作为增量
            if (operation == 0) {
          // 数量减一
          increment = -1;
        } else increment = operation;


          System.out.println(increment);

        Map<Long, Integer> map = Map.of(dessertId, increment);
        payFeignApi.updateDessertsStorage(map);

        // 更新数量
        Long newQuantity = hashOps.increment(cartKey, dessertKey, increment);

        // 检查新的数量，如果小于或等于0，可能需要从购物车中移除该项
        if (newQuantity <= 0) {
          hashOps.delete(cartKey, dessertKey);
          return false;  // 表示甜品已从购物车中移除
        }
      }catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        redisLock.unlock();
      }
      return true;  // 表示更新成功
    }
}

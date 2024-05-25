package tech.wetech.order.producer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.wetech.order.producer.model.Order;

import java.util.List;

/**
* @author Jansonci
* @description 针对表【order】的数据库操作Service
* @createDate 2024-04-28 20:13:36
*/
public interface OrderService extends IService<Order> {
  boolean saveOrderWithDetails(Order order);

  Order selectOrderWithDetails(Long orderId);

  List<Order> selectOrderByUser(Long userId);

  List<Order> selectOrdersByUserWithSql(Long userId);

  Order selectOrderWithDetailsByOrderId(Long orderId);

  void deleteOrderWithDetailsByOrderId(Long orderId);

}

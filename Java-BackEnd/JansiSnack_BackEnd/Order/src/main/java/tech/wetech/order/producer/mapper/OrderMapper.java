package tech.wetech.order.producer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import tech.wetech.order.producer.model.Order;

import java.util.List;

/**
* @author Jansonci
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-04-28 20:13:36
* @Entity generator.domain.Order
*/
public interface OrderMapper extends BaseMapper<Order> {
  Order selectOrderWithDetails(Long orderId);

  List<Order> selectOrdersWithDetailsByUser(Long userId);

  Order selectOrderWithDetailsByOrderId(Long orderId);

  void deleteOrderWithDetailsByOrderId(Long orderId);

}





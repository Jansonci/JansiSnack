package tech.wetech.order.producer.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.order.producer.mapper.OrderMapper;
import tech.wetech.order.producer.model.Order;
import tech.wetech.order.producer.model.OrderDetail;
import tech.wetech.order.producer.service.OrderDetailService;
import tech.wetech.order.producer.service.OrderService;

import java.util.ArrayList;
import java.util.List;

/**
* @author Jansonci
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-04-28 20:13:36
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderDetailService orderDetailService; // 假设你有一个OrderDetailService来处理订单详情

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrderWithDetails(Order order) {
      if (order.getId() != null) {
        for (OrderDetail detail : order.getOrderDetail()) {
          detail.setOrderId(order.getId()); // 确保详情链接到订单的ID
          orderDetailService.save(detail); // 保存订单详情
        }
      }
      return true;
    }

    public List<Order> selectOrderByUser(Long userId) {
      QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
      queryWrapper.select("order_id");  // 只查询order_id字段
      queryWrapper.eq("customer_id", userId);
      queryWrapper.orderByAsc("order_date"); // 使用orderByDesc可以改为降序排序
      List<Long> orderIds = orderMapper.selectObjs(queryWrapper)
        .stream()
        .map(id -> (Long) id)
        .toList();
      List<Order> orders = new ArrayList<>();
      for (Long orderId: orderIds){
        orders.add(selectOrderWithDetails(orderId));
      }
      return orders;
    }

    public List<Order> selectOrdersByUserWithSql(Long userId) {
      return orderMapper.selectOrdersWithDetailsByUser(userId);
    }

    public Order selectOrderWithDetails(Long orderId){
      return orderMapper.selectOrderWithDetails(orderId);
     }

    public  Order selectOrderWithDetailsByOrderId(Long orderId){
      return orderMapper.selectOrderWithDetailsByOrderId(orderId);
    };

    public void deleteOrderWithDetailsByOrderId(Long orderId){
      orderMapper.deleteOrderWithDetailsByOrderId(orderId);
    }
}





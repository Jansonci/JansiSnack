package tech.wetech.order.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import tech.wetech.order.producer.model.OrderDetail;

import java.util.List;

/**
* @author Jansonci
* @description 针对表【order_detail】的数据库操作Mapper
* @createDate 2024-04-29 10:23:24
* @Entity generator.domain.OrderDetail
*/
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    List<OrderDetail> selectByOrderId(Long orderId);
}





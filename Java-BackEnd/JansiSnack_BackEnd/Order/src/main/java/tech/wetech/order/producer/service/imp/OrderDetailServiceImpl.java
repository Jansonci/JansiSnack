package tech.wetech.order.producer.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.wetech.order.producer.mapper.OrderDetailMapper;
import tech.wetech.order.producer.model.OrderDetail;
import tech.wetech.order.producer.service.OrderDetailService;

/**
* @author Jansonci
* @description 针对表【order_detail】的数据库操作Service实现
* @createDate 2024-04-29 10:23:24
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService {

}





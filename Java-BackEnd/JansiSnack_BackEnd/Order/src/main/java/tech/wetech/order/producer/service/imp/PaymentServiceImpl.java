package tech.wetech.order.producer.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.wetech.order.producer.mapper.PaymentMapper;
import tech.wetech.order.producer.model.Payment;
import tech.wetech.order.producer.service.PaymentService;

/**
* @author Jansonci
* @description 针对表【payment】的数据库操作Service实现
* @createDate 2024-04-28 20:13:36
*/
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment>
    implements PaymentService {

}





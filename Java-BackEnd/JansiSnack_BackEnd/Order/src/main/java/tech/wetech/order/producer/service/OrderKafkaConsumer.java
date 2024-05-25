package tech.wetech.order.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.api.apis.PayFeignApi;
import tech.wetech.api.apis.UserFeignApi;
import tech.wetech.api.model.User;
import tech.wetech.order.producer.model.Order;
import tech.wetech.order.producer.model.Payment;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Jansonci
 */
@Service
@Slf4j
public class OrderKafkaConsumer {
  @Resource
  private PayFeignApi payFeignApi;
  @Resource
  private UserFeignApi userFeignApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  private final OrderService orderService;

  private final PaymentService paymentService;

  public OrderKafkaConsumer(OrderService orderService, PaymentService paymentService) {
    this.orderService = orderService;
    this.paymentService = paymentService;
  }

  @KafkaListener(topics = "dessert-order")
  @Transactional
  public void processOrder(String jsonOrder) throws JsonProcessingException {
    try {
      Order order = objectMapper.readValue(jsonOrder, Order.class);
      boolean saved = orderService.saveOrderWithDetails(order);
      System.out.println("Order processed: " + saved);
    } catch (Exception e) {
      e.printStackTrace();
      throw e; // Ensure transaction rollback on exception
    }
  }

  @KafkaListener(topics = "dessert-payment")
  @GlobalTransactional(name = "PaymentTransaction",rollbackFor = Exception.class) //AT
  public void processPayment(String jsonOrder) throws JsonProcessingException {
    try {
      String xid = RootContext.getXID();
      //1 新建订单
      log.info("---------------开始处理支付: "+"\t"+"xid: "+xid);
      Payment payment = objectMapper.readValue(jsonOrder, Payment.class);
      Order order = orderService.selectOrderWithDetails(payment.getOrderId());
      System.out.println(order);
      userFeignApi.updateUserField(new User.UpdateUserRequest1(order.getCustomerId(), "balance",
        (Objects.requireNonNull(userFeignApi.findUser(order.getCustomerId()).getBody()).balance().subtract(payment.getAmount()))));
      order.setState(Order.State.PAYED);
      orderService.updateById(order);
      paymentService.save(payment);
      Map<Long, Integer> paymentInfo =order.getOrderDetail().stream().map(
          orderDetail -> new AbstractMap.SimpleEntry<>(orderDetail.getDessertId(), orderDetail.getQuantity()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      payFeignApi.updateDessertsStorage(paymentInfo);
      stringRedisTemplate.delete("cart:" + order.getCustomerId());
    } catch (Exception e) {
      e.printStackTrace();
      throw e; // Ensure transaction rollback on exception
    }
  }
}

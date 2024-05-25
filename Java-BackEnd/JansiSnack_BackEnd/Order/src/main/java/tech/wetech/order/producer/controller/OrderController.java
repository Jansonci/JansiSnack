package tech.wetech.order.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.wetech.api.apis.PayFeignApi;
import tech.wetech.api.dto.DessertDTO;
import tech.wetech.order.producer.model.*;
import tech.wetech.order.producer.service.CartService;
import tech.wetech.order.producer.service.OrderService;
import tech.wetech.order.producer.service.PaymentService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Jansonci
 */
@RestController
@RequestMapping("/order")
public class OrderController {
  @Resource
  private OrderService orderService;

  @Resource
  private PaymentService paymentService;

  @Resource
  private CartService cartService;

  @Resource
  private PayFeignApi payFeignApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  KafkaTemplate<String, String> kafka;

  @PostMapping(value = "/createWithKafkaa")
  @Transactional
  public ResponseEntity<Object> createOrderWithKafkaa(@RequestParam Long userId, @RequestParam String address, @RequestParam Long requireTime, @RequestParam BigDecimal shippingCost) {
    try {
      Map<Long, Integer> orderDetails = this.getCart(userId).getBody();
      BigDecimal amount = BigDecimal.ZERO;

      Set<Long> dessertIds = new HashSet<>(orderDetails.keySet());
      Map<Long, BigDecimal> dessertsPrices = Optional.ofNullable(payFeignApi.findSomeDesserts(dessertIds))
        .map(HttpEntity::getBody)
        .orElseGet(Collections::emptySet)
        .stream()
        .collect(Collectors.toMap(DessertDTO::id, dessert -> BigDecimal.valueOf(dessert.price())));

      List<OrderDetail> orderDetailsList = new ArrayList<>();
      for (Map.Entry<Long, Integer> entry : orderDetails.entrySet()) {
        BigDecimal price = dessertsPrices.get(entry.getKey());
        amount = amount.add(price.multiply(BigDecimal.valueOf(entry.getValue())));
        orderDetailsList.add(new OrderDetail(null, entry.getKey(), entry.getValue()));
      }

      Order order = new Order(userId, address, OffsetDateTime.now(), Order.State.UNPAYED, amount.add(shippingCost), requireTime);
      orderService.save(order);

      for (OrderDetail detail : orderDetailsList) {
        detail.setOrderId(order.getId());
      }
      order.setOrderDetails(orderDetailsList);

      String jsonOrder = objectMapper.writeValueAsString(order);
      kafka.send("dessert-order", jsonOrder);
      return ResponseEntity.ok(order.getId());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/submitWithKafka")
  @Transactional
  public ResponseEntity<Payment> submitPaymentWithKafka(@RequestParam Long orderId, @RequestParam Payment.PaymentMethod paymentMethod) {
      Payment payment;
      try {
          Order order = orderService.getById(orderId);
          payment = new Payment(orderId, paymentMethod, order.getTotalAmount(), OffsetDateTime.now());
          String jsonOrder = objectMapper.writeValueAsString(payment);
          kafka.send("dessert-payment", jsonOrder);
      } catch (JsonProcessingException e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
      return ResponseEntity.ok(payment);
  }

  @GetMapping("/getCart/{userId}")
  public ResponseEntity<Map<Long, Integer>> getCart(@PathVariable Long userId){
    return ResponseEntity.ok(cartService.getCart(userId));
  }

  @GetMapping("/getOrderr/{userId}")
  public ResponseEntity<List<OrderForCheck>> getOrderss(@PathVariable Long userId) {
    List<OrderForCheck> orderForChecks = new ArrayList<>();
    List<Order> orders = orderService.selectOrdersByUserWithSql(userId);
    System.out.println(orders.size());
    Set<Long> processedOrders = new HashSet<>();
    for (Order order : orders) {
      if (!processedOrders.add(order.getId())) {
        continue;
      }else {
        List<OrderDetail> orderDetails = order.getOrderDetail();
        List<OrderItemEntry> orderItems = new ArrayList<>();

        // 获取 DessertDTO 对象
        Set<Long> dessertIds = orderDetails.stream().map(OrderDetail::getDessertId).collect(Collectors.toSet());

        Set<DessertDTO> desserts = payFeignApi.findSomeDesserts(dessertIds).getBody();
        Map<Long, DessertDTO> dessertMap = desserts.stream()
          .collect(Collectors.toMap(DessertDTO::id, Function.identity()));

        // 构建 orderItems 列表
        for (OrderDetail detail : orderDetails) {
          DessertDTO dessert = dessertMap.get(detail.getDessertId());
          if (dessert != null) {
            orderItems.add(new OrderItemEntry(dessert, detail.getQuantity()));
          }
        }

        Long stillNeeds = order.getState() == Order.State.UNPAYED ? -1 :
          (order.getRequireTime()-Duration.between(order.getOrderDate(), OffsetDateTime.now()).getSeconds()<=0?0:order.getRequireTime()-Duration.between(order.getOrderDate(), OffsetDateTime.now()).getSeconds());
        orderForChecks.add(new OrderForCheck(order.getId(), orderItems, order.getOrderDate(), stillNeeds, order.getTotalAmount(), order.getAddress()));
      }
    }
    return ResponseEntity.ok(orderForChecks);
  }

  @GetMapping("/getCartt/{userId}")
  public ResponseEntity<Set<CartDetail>> getCart1(@PathVariable Long userId){
    Set<CartDetail> cartDetails = new HashSet<>();
    Map<Long, Integer> cartDetailsInfos = cartService.getCart(userId);
    for (Map.Entry<Long, Integer> cartDetailsInfo: cartDetailsInfos.entrySet()){
      CartDetail cartDetail = new CartDetail(payFeignApi.findDessertByName(cartDetailsInfo.getKey()).getBody(), cartDetailsInfo.getValue());
      cartDetails.add(cartDetail);
    }
    return ResponseEntity.ok(cartDetails);
  }

  @GetMapping("/getSingleOrder/{orderId}")
  public ResponseEntity<Order> getOrder(@PathVariable Long orderId){
    Order order = orderService.selectOrderWithDetailsByOrderId(orderId);
    return ResponseEntity.ok(order);
  }
  @PutMapping("/updateCart")
  public ResponseEntity<Boolean> updateCartRequest(@RequestBody updateCartRequest updateCartRequest){
    boolean result = cartService.updateCartRequest(updateCartRequest.userId, updateCartRequest.dessertId, updateCartRequest.operation);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/cancelOrder/{orderId}")
  public ResponseEntity<Boolean> cancelOrder(@PathVariable Long orderId){
    orderService.deleteOrderWithDetailsByOrderId(orderId);
    return ResponseEntity.ok().build();
  }
  public record updateCartRequest(@Nonnull Long userId, @Nonnull Long dessertId, Integer operation ){}
}

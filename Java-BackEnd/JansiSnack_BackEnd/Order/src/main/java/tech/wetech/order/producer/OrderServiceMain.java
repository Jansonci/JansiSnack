package tech.wetech.order.producer;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jansonci
 */
@SpringBootApplication
@EntityScan("tech.wetech.order.producer.model")
@MapperScan("tech.wetech.order.producer.mapper")
@ComponentScan(basePackages = {"tech.wetech.api","tech.wetech.order.producer", "tech.wetech.service"})
@EnableFeignClients(basePackages = "tech.wetech.api.apis")
@EnableDiscoveryClient //该注解用于向使用consul为注册中心时注册服务
public class OrderServiceMain {
  public static void main(String[] args) {
    SpringApplication.run(OrderServiceMain.class,args);
  }
  @Bean
  public MybatisPlusInterceptor plusInterceptor(){
    //mybatis-plus的插件集合 【加入到这个集合中即可，分页插件，乐观锁插件】
    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    //分页插件
    mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    //乐观锁【版本号插件】 mybatis-plus会在更新的时候，每次帮我们对比版本号字段和增加版本号+1
    mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    //防止全表删除和更新拦截器
    mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return  mybatisPlusInterceptor;
  }
}

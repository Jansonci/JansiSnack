package tech.wetech.dessert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jansonci
 */
@SpringBootApplication
@EntityScan("tech.wetech.api.model")
@ComponentScan(basePackages = {"tech.wetech.api","tech.wetech.dessert", "tech.wetech.service"})
@EnableDiscoveryClient //该注解用于向使用consul为注册中心时注册服务
@EnableFeignClients//启用feign客户端,定义服务+绑定接口，以声明式的方法优雅而简单的实现服务调用
public class DessertServiceMain
{
  public static void main(String[] args)
  {
    SpringApplication.run(DessertServiceMain.class,args);
  }
}

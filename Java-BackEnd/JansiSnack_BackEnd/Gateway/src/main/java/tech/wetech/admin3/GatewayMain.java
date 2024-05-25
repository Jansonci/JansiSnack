package tech.wetech.admin3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //服务注册和发现
public class GatewayMain {
  public static void main(String[] args) {
    SpringApplication.run(GatewayMain.class,args);
  }
}

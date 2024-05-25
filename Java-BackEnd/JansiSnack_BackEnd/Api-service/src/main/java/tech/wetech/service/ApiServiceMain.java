package tech.wetech.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("tech.wetech.api.model")
public class ApiServiceMain {
  public static void main(String[] args) {
    SpringApplication.run(ApiServiceMain.class, args);
  }
}

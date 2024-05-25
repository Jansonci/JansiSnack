package tech.wetech.service.infra;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjbi
 * 未使用
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Admin3", description = "A Flexible and Efficient Backend Framework for Java", version = "v1"))
@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer"
)
public class OpenApiConfig {

  @Bean
  public OpenAPI getOpenAPI() {
    return new OpenAPI().components(new Components()
      .addHeaders("Authorization", new Header().description("Auth header").schema(new StringSchema()))); // 向消息头中Authorization字段设置一个自动添加"bearer"的模版，然后
  }                                                                                                           // "bearer"和token一起组成完整的Authorization字段

}

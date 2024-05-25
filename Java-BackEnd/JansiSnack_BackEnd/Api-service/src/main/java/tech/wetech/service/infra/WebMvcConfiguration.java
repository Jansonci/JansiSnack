//package tech.wetech.service.infra;
//
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.*;
//import tech.wetech.api.common.BaseEntitySerializer;
//import tech.wetech.api.common.EventStore;
//import tech.wetech.service.service.SessionService;
//
//import java.time.format.DateTimeFormatter;
//
///**
// * @author cjbi
// * 未使用
// */
//@Configuration
//public class WebMvcConfiguration implements WebMvcConfigurer {
//
//  private final SessionService sessionService;
//  private final EventStore eventStore;
//
//  public WebMvcConfiguration(SessionService sessionService, EventStore eventStore) {
//    this.sessionService = sessionService;
//    this.eventStore = eventStore;
//  }
//
//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    InterceptorRegistration loginInterceptor = registry.addInterceptor(new AuthInterceptor(sessionService));
//    loginInterceptor.addPathPatterns("/**");
//    loginInterceptor.excludePathPatterns(
//      "/actuator/**"  // 添加此行以排除Actuator端点
//    );
//    loginInterceptor.excludePathPatterns(
//      "/storage/fetch/**",
//      "/storage/download/**",
//      "/login",
//      "/swagger-ui.html",
//      "/swagger-ui/**",
//      "/v3/api-docs/**",
//      "/assets/**",
//      "/favicon.ico",
//      "/avatar.jpg",
//      "/index.html",
//      "/",
//      "/desserts/**",
//      "/signUp",
//      "/users/**",
//      "/static/**"
//    );
//    InterceptorRegistration eventSubscribesInterceptor = registry.addInterceptor(new EventSubscribesInterceptor(eventStore, sessionService));
//    eventSubscribesInterceptor.addPathPatterns("/**");
//  }
//
//  @Override
//  public void addViewControllers(ViewControllerRegistry registry) {
//    registry.addViewController("/").setViewName("/index.html");
//  }
//
//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/webjars/admin3-ui/");
//    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//
//  }
//
//  @Bean
//  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//    return builder -> {
//      //jpa entity serializers
//      builder.serializers(BaseEntitySerializer.instance);
//
//      //datetime formatter
//      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//      DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//      //datetime deserializers
//      builder.deserializers(new LocalDateDeserializer(dateFormatter));
//      builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
//
//      //datetime serializers
//      builder.serializers(new LocalDateSerializer(dateFormatter));
//      builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
//    };
//  }
//
//}

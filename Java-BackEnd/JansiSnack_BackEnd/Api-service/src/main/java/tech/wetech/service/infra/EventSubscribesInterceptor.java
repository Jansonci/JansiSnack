package tech.wetech.service.infra;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.common.DomainEventPublisher;
import tech.wetech.api.common.EventStore;
import tech.wetech.api.event.ResourceDeleted;
import tech.wetech.api.event.ResourceUpdated;
import tech.wetech.api.event.RoleDeleted;
import tech.wetech.api.event.RoleUpdated;
import tech.wetech.service.service.SessionService;


/**
 * 通用事件处理拦截器，
 * @author cjbi
 */
public class EventSubscribesInterceptor implements HandlerInterceptor {
  private final EventStore eventStore;
  private final SessionService sessionService;

  public EventSubscribesInterceptor(EventStore eventStore, SessionService sessionService) {
    this.eventStore = eventStore;
    this.sessionService = sessionService;
  }

  /**
   * 认为存在漏洞，preHandle方法只是向线程本地DomainEventPublisher对象招纳了特定的subscriber，但并未将其与request相联系，不认为可以实现监听功能
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    DomainEventPublisher.instance().reset();
    DomainEventPublisher.instance().subscribe(DomainEvent.class, eventStore::append);
    //发生以下事件, 刷新会话
    DomainEventPublisher.instance().subscribe(RoleUpdated.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(RoleDeleted.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(ResourceUpdated.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(ResourceDeleted.class, event -> sessionService.refresh());
    return true;
  }


}

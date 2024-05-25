package tech.wetech.service.service.service;

import org.springframework.stereotype.Service;
import tech.wetech.api.common.*;
import tech.wetech.api.dto.UserinfoDTO;
import tech.wetech.api.model.StoredEvent;
import tech.wetech.service.repository.StoredEventRepository;
import tech.wetech.service.repository.UserRepository;
/**
 * @author Jansonci
 */
@Service
public class EventStoreService implements EventStore {

  private final StoredEventRepository storedEventRepository;
  private final UserRepository userRepository;

  public EventStoreService(StoredEventRepository storedEventRepository, UserRepository userRepository) {
    this.storedEventRepository = storedEventRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void append(DomainEvent aDomainEvent) {
    StoredEvent storedEvent = new StoredEvent();
    storedEvent.setEventBody(JsonUtils.stringify(aDomainEvent));
    storedEvent.setOccurredOn(aDomainEvent.occurredOn());
    storedEvent.setTypeName(aDomainEvent.getClass().getTypeName());
    UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
    if (userInfo != null) {
      storedEvent.setUser(userRepository.getReferenceById(userInfo.userId()));
    }
    storedEventRepository.save(storedEvent);
  }

}

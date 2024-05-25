package tech.wetech.api.event;

import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.User;

/**
 * @author cjbi
 */
public record UserCreated(User user) implements DomainEvent {
}

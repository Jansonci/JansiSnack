package tech.wetech.api.event;


import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.User;

/**
 * @author cjbi
 */
public record UserUpdated(User user) implements DomainEvent {
}

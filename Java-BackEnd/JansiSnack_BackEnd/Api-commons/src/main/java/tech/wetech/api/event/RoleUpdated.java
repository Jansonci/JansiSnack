package tech.wetech.api.event;

import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.Role;

/**
 * @author cjbi
 */
public record RoleUpdated(Role role) implements DomainEvent {
}

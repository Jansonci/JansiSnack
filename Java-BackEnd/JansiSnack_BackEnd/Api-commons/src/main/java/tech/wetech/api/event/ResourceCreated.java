package tech.wetech.api.event;


import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.Resource;

/**
 * @author cjbi
 */
public record ResourceCreated(Resource resource) implements DomainEvent {
}

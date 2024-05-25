package tech.wetech.api.event;


import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.StorageConfig;

/**
 * @author cjbi
 */
public record StorageConfigDeleted(StorageConfig config) implements DomainEvent {
}

package tech.wetech.api.event;


import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.model.Organization;

/**
 * @author cjbi
 */
public record OrganizationDeleted(Organization organization) implements DomainEvent {
}

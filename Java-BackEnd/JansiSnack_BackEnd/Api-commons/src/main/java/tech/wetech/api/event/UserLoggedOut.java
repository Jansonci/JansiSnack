package tech.wetech.api.event;

import tech.wetech.api.common.DomainEvent;
import tech.wetech.api.dto.UserinfoDTO;

/**
 * @author cjbi
 */
public record UserLoggedOut(UserinfoDTO userinfo) implements DomainEvent {
}

package tech.wetech.api.dto;


import tech.wetech.api.model.User;

import java.time.LocalDateTime;

/**
 * @author cjbi
 */
public record LogDTO(Long id, String content, String eventBody, String typeName, LocalDateTime occurredOn, User user) {
}

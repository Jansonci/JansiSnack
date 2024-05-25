package tech.wetech.api.dto;


import tech.wetech.api.model.User;

import java.time.LocalDateTime;

/**
 * @author cjbi
 */
public record OrgUserDTO(Long id,
                         String username,
                         String avatar,
                         User.Gender gender,
                         User.State state,
                         String orgFullName,
                         LocalDateTime createdTime) {
}

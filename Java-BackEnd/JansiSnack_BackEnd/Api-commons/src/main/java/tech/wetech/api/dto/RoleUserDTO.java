package tech.wetech.api.dto;


import tech.wetech.api.model.Role;
import tech.wetech.api.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author cjbi
 */
public record RoleUserDTO(Long id,
                          String username,
                          String avatar,
                          User.Gender gender,
                          User.State state,
                          Set<Role> roles,
                          LocalDateTime createdTime) {

}

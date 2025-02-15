package train.booking.train.booking.service;

import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;

import java.util.Optional;

public interface RoleService  {
    Role save(Role userRole);

    Optional<Role> findByRoleType(RoleType userRole);
}

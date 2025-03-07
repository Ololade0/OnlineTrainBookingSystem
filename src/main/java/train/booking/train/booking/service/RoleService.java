package train.booking.train.booking.service;

import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

public interface RoleService  {
    Role save(Role userRole);

    Optional<Role> findByRoleType(RoleType userRole) throws RoleNotFoundException;
}

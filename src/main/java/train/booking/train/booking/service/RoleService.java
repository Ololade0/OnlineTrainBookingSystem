package train.booking.train.booking.service;

import train.booking.train.booking.dto.RoleDTo;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;

import javax.management.relation.RoleNotFoundException;

public interface RoleService  {
    BaseResponse save(RoleDTo roleDTo);
    Role findByRoleType(RoleType roleType) throws RoleNotFoundException;
    BaseResponse update(RoleDTo roleDTo);

    BaseResponse delete(RoleType roleType);
}

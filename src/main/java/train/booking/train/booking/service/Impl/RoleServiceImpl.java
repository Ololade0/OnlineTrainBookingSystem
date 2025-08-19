package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.RoleDTo;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseCodes;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.RoleException;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.RoleRepository;
import train.booking.train.booking.service.RoleService;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;



    @Override
    public BaseResponse  save(RoleDTo roleDTo) {
        if(roleRepository.existsByRoleType(roleDTo.getRoleType())){
            log.warn("Attempted to create duplicate role:  {}", roleDTo.getRoleType());
            return ResponseUtil.response(ResponseCodes.ALREADY_EXISTS, "Role Already Exist", null);

        }
         Role newRole =  Role.builder()
                .roleType(roleDTo.getRoleType())
                .build();
       roleRepository.save(newRole);
       RoleDTo response = RoleDTo.builder()
               .roleType(newRole.getRoleType())
               .build();
       log.info("New Role created : {}", newRole.getRoleType());
       return ResponseUtil.success("Role sucessfully created", response);
    }


    @Override
    public Role findByRoleType(RoleType roleType) throws RoleNotFoundException {
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType));
    }

    @Override
    public BaseResponse update(RoleDTo roleDTo) {
        Optional<Role> optionalRole = roleRepository.findByRoleType(roleDTo.getRoleType());
        if (optionalRole.isEmpty()) {
            log.warn("Role not found for update: {}", roleDTo.getRoleType());
            throw new RoleException("Role cannot be found");
        }
        optionalRole.get().setRoleType(roleDTo.getRoleType());
        roleRepository.save(optionalRole.get());
        RoleDTo response = RoleDTo.builder()
                .roleType(optionalRole.get().getRoleType())
                .build();

        log.info("Role updated: {}", optionalRole.get().getRoleType());
        return ResponseUtil.success("Role successfully updated", response);
    }

    @Override
    public BaseResponse delete(RoleType roleType) {
        Optional<Role> optionalRole = roleRepository.findByRoleType(roleType);
        if (optionalRole.isEmpty()) {
            log.warn("Role not found for deletion: {}", roleType);
            throw new RoleException("Role cannot be found");
        }
        roleRepository.delete(optionalRole.get());
        log.info("Role deleted: {}", roleType);
        return ResponseUtil.success("Role successfully deleted", null);
    }
    @Override
    public List<RoleType> getAllRoles() {
        List<RoleType> roles = Arrays.asList(RoleType.values());
        if (roles.isEmpty()) {
            throw new RuntimeException("No roles found"); // Or create custom exception
        }
        return roles;
    }

}

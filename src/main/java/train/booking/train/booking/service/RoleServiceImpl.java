package train.booking.train.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.exceptions.RoleAlraedyExistException;
import train.booking.train.booking.exceptions.RoleException;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.RoleRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;



    @Override
    public Role save(Role userRole) {
        if(userRole == null){
            throw new RoleException("Role cannot be null");
        }

        if(roleRepository.existsByRoleType(userRole.getRoleType())){
            throw new RoleAlraedyExistException("Role " + userRole.getRoleType() + "already exist");

        }
         Role newRole =  Role.builder()
                .roleType(userRole.getRoleType())
                .build();
        return roleRepository.save(newRole);
    }

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) throws RoleNotFoundException {

        Role assignedRole = roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType));
        return Optional.ofNullable(assignedRole);
    }



}

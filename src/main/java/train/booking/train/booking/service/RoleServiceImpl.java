package train.booking.train.booking.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.RoleRepository;

;import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;



    @Override
    public Role save(Role userRole) {
        return roleRepository.save(userRole);
    }

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) {
        return roleRepository.findByRoleType(roleType);
    }

}

package train.booking.train.booking.service.Impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;
import train.booking.train.booking.service.AdminService;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.RoleService;
import train.booking.train.booking.utils.Helper;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    private final NotificationService notificationService;

    private final Helper helper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;



    @Override
    public BaseResponse superAdminSignUp(UserDTO userDTO) throws UnirestException {
        try{
//            validateStaffInfo(userDTO);
//            validateStaffEmail(userDTO.getEmail());
//            validateStaffPasswordStrength(userDTO.getPassword());
            String activationToken = UUID.randomUUID().toString();
            User signupUser = User.builder()
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .email(userDTO.getEmail())
                    .gender(userDTO.getGender())
                    .dateOfBirth(userDTO.getDateOfBirth())
                    .identificationType(userDTO.getIdentificationType())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                    .idNumber(userDTO.getIdNumber())
                    .isVerified(false)
                    .activationToken(activationToken)
                    .roleHashSet(new HashSet<>())
                    .build();
            Role assignedRole = roleService.findByRoleType(RoleType.SUPERADMIN_ROLE);
            signupUser.getRoleHashSet().add(assignedRole);
            userRepository.save(signupUser);
            Map m = getMap(signupUser);
            notificationService.sendEmailV3(signupUser.getEmail(), "ACTIVATION LINK", helper.build(m, "account-activation-email"));
            UserDTO responseDto = UserDTO.builder()
                    .firstName(signupUser.getFirstName())
                    .lastName(signupUser.getLastName())
                    .email(signupUser.getEmail())
                    .roles(signupUser.getRoleHashSet())
                    .build();
            return ResponseUtil.success("Account sucessfully created", responseDto);
        }
        catch (Exception e) {
            log.error("Error during super admin sign-up: {}", e.getMessage());
            return ResponseUtil.failed("Sign-up failed due to an unexpected error.", e);
        }

    }

    @Override
    public Page<FindAllByRolesDTO> findAllByRole(RoleType roleType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> usersPage = userRepository.findAllByRoleType(roleType, pageable);
        return usersPage.map(user -> FindAllByRolesDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleHashSet(user.getRoleHashSet())
                .isVerified(user.isVerified())
                .build()
        );
    }

    private static Map getMap(User signupUser) {
        Map m = new HashMap<>();
        m.put("firstName", signupUser.getFirstName());
        m.put("lastName", signupUser.getLastName());
        m.put("activationToken", signupUser.getActivationToken());
        log.info("Email to send activation to: {}", signupUser.getEmail());
        return m;
    }

    private void validateStaffInfo(UserDTO userDTO) {
        if (!Objects.equals(userDTO.getPassword().trim(), userDTO.getConfirmPassword().trim())) {
            throw new PasswordDoesNotMatchException("The passwords you entered do not match. Please ensure both fields are identical.");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistException("User with the email already Exist");
        }
        if (userDTO.getIdNumber() == null || userDTO.getIdNumber().length() < 10 || userDTO.getIdNumber().length() > 15) {
            throw new InvalidIdNumber("IDss number must be between 10 and 15 characters.");
        }
        if (userRepository.existsByIdNumber(userDTO.getIdNumber())) {
            throw new IdNumberAlreadyExist("user identification number already exist");

        }
    }

    private void validateStaffPasswordStrength(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new WeakPasswordException("Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character.");
        }
    }

    private void validateStaffEmail(String email) {
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }


}

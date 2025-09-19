package train.booking.train.booking.service.Impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;
import train.booking.train.booking.service.AdminService;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.RoleService;
import train.booking.train.booking.utils.Helper;

import javax.management.relation.RoleNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Value("${activation.url}")
    private String ACTIVATION_URL;


    @Value("${base.url}")
    private String BASE_URL;

    private final UserRepository userRepository;
    private final RoleService roleService;

    private final NotificationService notificationService;

    private final Helper helper;
    private final PasswordEncoder passwordEncoder;


    public BaseResponse superAdminSignUp(UserDTO userDTO) throws RoleNotFoundException {
        validateStaffInfo(userDTO);
        validateStaffEmail(userDTO.getEmail());
        validateStaffPasswordStrength(userDTO.getPassword());

        String activationToken = UUID.randomUUID().toString();
        String activationLink = BASE_URL + ACTIVATION_URL + "?token=" + activationToken;

        User signupUser = User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .gender(userDTO.getGender())
                .dateOfBirth(userDTO.getDateOfBirth())
                .identificationType(userDTO.getIdentificationType())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .idNumber(userDTO.getIdNumber())
                .isVerified(false)
                .activationToken(activationToken)
                .roleHashSet(new HashSet<>())
                .build();
        Role assignedRole = roleService.findByRoleType(RoleType.SUPERADMIN_ROLE);
        signupUser.getRoleHashSet().add(assignedRole);

        userRepository.save(signupUser);
        Map<String, Object> model = getMap(signupUser, activationLink);
        notificationService.sendEmailV3(signupUser.getEmail(), "ACTIVATION LINK", helper.build(model, "account-activation-email"));

        log.info("ACTIVATION LINK: {}", activationLink);

        UserDTO responseDto = UserDTO.builder()
                .firstName(signupUser.getFirstName())
                .lastName(signupUser.getLastName())
                .email(signupUser.getEmail())
                .roles(signupUser.getRoleHashSet())
                .build();

        return ResponseUtil.success("Account successfully created", responseDto);
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


    private static Map getMap(User signupUser, String activationLink) {
        Map m = new HashMap<>();
        m.put("firstName", signupUser.getFirstName());
        m.put("lastName", signupUser.getLastName());
        m.put("activationLink", activationLink);
        log.info("Email to send activation to: {}", signupUser.getEmail());
        log.info("MAIL ACTIVATION LINK. {}", activationLink);
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


    @Override
    public List<IdentificationType> getAllIdenticationTypes() {
        List<IdentificationType> identificationTypes = Arrays.asList(IdentificationType.values());
        if (identificationTypes.isEmpty()) {
            throw new IdentificationException("No identification found");
        }
        return identificationTypes;
    }

    @Override
    public List<GenderType> getAllGenders() {
        List<GenderType> genderTypes = Arrays.asList(GenderType.values());
        if (genderTypes.isEmpty()) {
            throw new GenderTypeException("No genders found");
        }
        return genderTypes;

    }

    @Override
    public List<AgeRange> getAllAgeRange() {
        List<AgeRange> ageRangeList = Arrays.asList(AgeRange.values());
        if(ageRangeList.isEmpty()){
            throw new AgeRangeExeption("No Age range found");
        }
        return ageRangeList;
    }


}







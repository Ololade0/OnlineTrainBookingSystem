package train.booking.train.booking.service.Impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseCodes;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.RoleService;
import train.booking.train.booking.service.UserService;
import train.booking.train.booking.utils.Helper;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    private final NotificationService notificationService;

    private final Helper helper;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public BaseResponse signUpNewUser(UserDTO userDTO) throws UnirestException, RoleNotFoundException {
        // Validate user input
        validateUserInfo(userDTO);
        validateEmail(userDTO.getEmail());
        validatePasswordStrength(userDTO.getPassword());

        // Default role
        RoleType requestedRoleType = Optional.ofNullable(userDTO.getRoleType())
                .orElse(RoleType.USER_ROLE);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated AND not anonymous
        boolean isRealUser = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (isRealUser) {
            User currentUser = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(() -> new UnAuthorizedException("Unauthorized"));

            // Only SUPERADMIN can assign roles other than USER_ROLE
            if (requestedRoleType != RoleType.USER_ROLE && !currentUser.hasRole(RoleType.SUPERADMIN_ROLE)) {
                requestedRoleType = RoleType.USER_ROLE;
            }
        } else {
            // If not logged in, default to USER_ROLE
            requestedRoleType = RoleType.USER_ROLE;
        }

        // Build new user entity
        String activationToken = UUID.randomUUID().toString();
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

        // Assign the role
        Role assignedRole = roleService.findByRoleType(requestedRoleType);
        signupUser.getRoleHashSet().add(assignedRole);

        log.info("Assigning role {} to new user {}", requestedRoleType, userDTO.getEmail());

        // Save the user
        userRepository.save(signupUser);

        // Send activation email
        Map<String, Object> emailMap = getMap(signupUser);
        notificationService.sendEmailV3(signupUser.getEmail(), "ACTIVATION LINK", helper.build(emailMap, "account-activation-email"));

        // Build response DTO
        UserDTO responseDto = UserDTO.builder()
                .firstName(signupUser.getFirstName())
                .lastName(signupUser.getLastName())
                .email(signupUser.getEmail())
                .roles(signupUser.getRoleHashSet())
                .build();

        return ResponseUtil.success("Account successfully created", responseDto);
    }



    private static Map getMap(User signupUser) {
        Map m = new HashMap<>();
        m.put("FirstName", signupUser.getFirstName());
        m.put("token",  "" + signupUser.getActivationToken());
        log.info("Email to send activation to: {}", signupUser.getEmail());
        return m;
    }


    private void validateUserInfo(UserDTO userDTO) {
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

    private void validatePasswordStrength(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new WeakPasswordException("Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character.");
        }
    }

    private void validateEmail(String email) {
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }


    public User findUserByEmail(String email) {
      return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserCannotBeFoundException("User with email " + email + " not found"));

    }



    @Override
    public User findUserByEmailOrNull(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findUserByEmail(email).orElseThrow(() -> new UserCannotBeFoundException("User cannot be found")));
        return user.get();
    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserCannotBeFoundException("User with id " + userId + " not found"));
        return user;
    }

    @Override
    public Optional<User> findUserByActivationToken(String token) {
        return Optional.ofNullable(userRepository.findByActivationToken(token)
                .orElseThrow(() -> new UserCannotBeFoundException("User with token cannot be found ")));
    }


    private UserLoginResponse buildSuccessfulLoginResponse(User user) {
        return UserLoginResponse.builder()
                .code(200)
                .message("Login successful")
                .build();

    }
    @Override
    public String activateAccount(String token) throws UnirestException {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired activation token"));
        if(user.isVerified()){
            return "Account is already activated";
        }
        user.setVerified(true);
        user.setActivationToken(null);
        userRepository.save(user);
//        notificationService.sendEmailV3(user.getEmail(),"ACCOUNT ACTIVATION", "" );
        return "Account as been sucessfully activated";

    }

    @Override
    public BaseResponse updateUserProfile(UserDTO userDTO, Long userId) {
        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isPresent()) {
            User savedUser = foundUser.get();

            if (userDTO.getFirstName() != null && !userDTO.getFirstName().isBlank()) {
                savedUser.setFirstName(userDTO.getFirstName());
            }

            if (userDTO.getLastName() != null && !userDTO.getLastName().isBlank()) {
                savedUser.setLastName(userDTO.getLastName());
            }

            if (userDTO.getEmail() != null && userDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                savedUser.setEmail(userDTO.getEmail());
            }

            if (userDTO.getGender() != null) {
                savedUser.setGender(userDTO.getGender());
            }

            if (userDTO.getDateOfBirth() != null) {
                savedUser.setDateOfBirth(userDTO.getDateOfBirth());
            }

            if (userDTO.getIdentificationType() != null) {
                savedUser.setIdentificationType(userDTO.getIdentificationType());
            }

            if (userDTO.getPhoneNumber() != null && userDTO.getPhoneNumber().length() >= 10) {
                savedUser.setPhoneNumber(userDTO.getPhoneNumber());
            }

            if (userDTO.getIdNumber() != null && !userDTO.getIdNumber().isBlank()) {
                savedUser.setIdNumber(userDTO.getIdNumber());
            }
            savedUser.setUpdatedAt(LocalDateTime.now());

            userRepository.save(savedUser);
            return response(savedUser);
        }

        return ResponseUtil.failed("User not found", null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }


    private BaseResponse response (User savedUser){
        UserDTO updatedDto = new UserDTO();
        updatedDto.setFirstName(savedUser.getFirstName());
        updatedDto.setLastName(savedUser.getLastName());
        updatedDto.setEmail(savedUser.getEmail());
        updatedDto.setGender(savedUser.getGender());
        updatedDto.setDateOfBirth(savedUser.getDateOfBirth());
        updatedDto.setIdentificationType(savedUser.getIdentificationType());
        updatedDto.setPhoneNumber(savedUser.getPhoneNumber());
        updatedDto.setIdNumber(savedUser.getIdNumber());
        return ResponseUtil.success("User updated successfully", null);
    }


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findUserByEmail(username);
//        if(user!= null){
//            return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), getAuthorities(user.get().getRoleHashSet()));
//        }
//        return null;
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roleHashSet) {
//        return roleHashSet.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toSet());
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(
                email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ✅ Convert RoleType to authorities (SUPERADMIN_ROLE → SUPERADMIN_ROLE)
        Set<GrantedAuthority> authorities = user.getRoleHashSet().stream()
                .map(Role::getRoleType)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    public Page<User> getAllNonUserAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllWithAtLeastOneNonUserRole(pageable);
    }

}





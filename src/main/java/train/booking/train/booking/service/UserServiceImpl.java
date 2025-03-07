package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.request.UserLoginRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    private final NotificationService notificationService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public SignUpUserResponse superAdminSignUp(SignUpRequest signUpRequest) throws UnirestException {
        try{

        validateUserInfo(signUpRequest);
        validateEmail(signUpRequest.getEmail());
        validatePasswordStrength(signUpRequest.getPassword());
        String activationToken = UUID.randomUUID().toString();

        User signupUser = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .dateOfBirth(signUpRequest.getDateOfBirth())
                .identificationType(signUpRequest.getIdentificationType())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .confirmPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .idNumber(signUpRequest.getIdNumber())
                .enabled(false)
                .activationToken(activationToken)
                .roleHashSet(new HashSet<>())
                .build();

        Role assignedRole = roleService.findByRoleType(RoleType.SUPERADMIN_ROLE).get();
        signupUser.getRoleHashSet().add(assignedRole);
        userRepository.save(signupUser);
        log.info("User Details: {}", signupUser);


        notificationService.sendActivationEmail(signupUser.getEmail(), signupUser.getFirstName(), activationToken);
        return getSignUpUserResponse(signupUser);
        } catch (Exception e) {
            log.error("Error during super admin sign-up: {}", e.getMessage());
            throw new RuntimeException("Sign-up failed due to an unexpected error.", e);
        }
    }


    @Override
    @Transactional
    public SignUpUserResponse signUp(SignUpRequest signUpRequest) throws UnirestException, RoleNotFoundException {

        validateUserInfo(signUpRequest);
        validateEmail(signUpRequest.getEmail());
        validatePasswordStrength(signUpRequest.getPassword());
        String activationToken = UUID.randomUUID().toString();

        RoleType requestedRoleType = Optional.ofNullable(signUpRequest.getRoleType()).orElse(RoleType.USER_ROLE);

        log.info("Requested RoleType: {}", requestedRoleType);

        if (requestedRoleType != RoleType.USER_ROLE) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnAuthorizedException("Only SUPERADMIN can create accounts for roles other than USER_ROLE!");
            }

            User currentUser = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(() -> new UnAuthorizedException("Unauthorized"));

            if (!currentUser.hasRole(RoleType.SUPERADMIN_ROLE)) {
                throw new UnAuthorizedException("Only SUPERADMIN can assign roles other than USER_ROLE!");
            }
        }
        User signupUser = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .dateOfBirth(signUpRequest.getDateOfBirth())
                .identificationType(signUpRequest.getIdentificationType())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .idNumber(signUpRequest.getIdNumber())
                .enabled(false)
                .activationToken(activationToken)
                .roleHashSet(new HashSet<>())
                .build();
      Optional<Role> assignedRole =   roleService.findByRoleType(requestedRoleType);
        signupUser.getRoleHashSet().add(assignedRole.get());
        log.info("Assigning role {} to new user {}", requestedRoleType, signUpRequest.getEmail());

        userRepository.save(signupUser);
        notificationService.sendActivationEmail(signupUser.getEmail(), signupUser.getFirstName(), activationToken);
        return getSignUpUserResponse(signupUser);
    }

    private static SignUpUserResponse getSignUpUserResponse(User signupUser) {
        return SignUpUserResponse.builder()
                .id(signupUser.getId())
                .firstName(signupUser.getFirstName())
                .lastName(signupUser.getLastName())
                .email(signupUser.getEmail())
                .gender(signupUser.getGender())
                .dateOfBirth(signupUser.getDateOfBirth())
                .idNumber(signupUser.getIdNumber())
                .phoneNumber(signupUser.getPhoneNumber())
                .message("Account successfully created")
                .roles(signupUser.getRoleHashSet())
                .build();
    }



    private void validateUserInfo(SignUpRequest signUpRequest) {
        if (!Objects.equals(signUpRequest.getPassword().trim(), signUpRequest.getConfirmPassword().trim())) {
            throw new PasswordDoesNotMatchException("The passwords you entered do not match. Please ensure both fields are identical.");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistException("User with the email already Exist");
        }
        if (signUpRequest.getIdNumber() == null || signUpRequest.getIdNumber().length() < 10 || signUpRequest.getIdNumber().length() > 15) {
            throw new InvalidIdNumber("IDss number must be between 10 and 15 characters.");
        }
        if (userRepository.existsByIdNumber(signUpRequest.getIdNumber())) {
            throw new IdNumberAlreadyExist("user identification number already exist");

        }
    }

    private void validatePasswordStrength(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new SeatAlreadyBookedException("Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character.");
        }
    }

    private void validateEmail(String email) {
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }


    @Override
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        return user.orElseThrow(() -> new UserCannotBeFoundException("User with email " + email + " not found"));
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new UserCannotBeFoundException("User with id " + userId + " cannot be found"));
    }

    public User findUserByEmailOrNull(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        return user.orElse(null);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequestModel) {
        var user = userRepository.findUserByEmail(userLoginRequestModel.getEmail());
        if (user.isPresent() && bCryptPasswordEncoder.matches(userLoginRequestModel.getPassword(), user.get().getPassword())) {
            return buildSuccessfulLoginResponse(user.get());
        }
        throw new IllegalArgumentException("Invalid email or password");

    }

    @Override
    public void disableUser(String email) {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserCannotBeFoundException("User with email " + email + " not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }


    public void enableUser(String email) {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserCannotBeFoundException("User with email " + email + " not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String email) {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new UserCannotBeFoundException("User with email " + email + " not found"));
        userRepository.delete(user);
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

    public String activateAccount(String token) throws UnirestException {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired activation token"));

        user.setEnabled(true);
        user.setActivationToken(null);
       userRepository.save(user);
       notificationService.sendMail(user.getEmail(), user.getFirstName());
       return "Account sucessfully activated";

    }






    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username).orElse(null);

        /*
        TODO

        if (!user.isEnabled()) {
            throw new UserIsDisabledException("User account is disabled");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                Set.of(new SimpleGrantedAuthority((user.getRoleHashSet()).toString())));

       }
         */

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthorities(user.getRoleHashSet()));
        }

        return null;

    }


    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roleHashSet) {
        return roleHashSet.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toSet());
    }

}





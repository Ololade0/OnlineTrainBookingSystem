package train.booking.train.booking.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public SignUpUserResponse superAdminSignUp(SignUpRequest signUpRequest) {
        User signupUser = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .DateOfBirth(signUpRequest.getDateOfBirth())
                .identificationType(signUpRequest.getIdentificationType())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .idNumber(signUpRequest.getIdNumber())
                .confirmPassword(bCryptPasswordEncoder.encode(signUpRequest.getConfirmPassword()))
                .roleHashSet(new HashSet<>())
                .build();

        Role assignedRole = roleService.save(new Role(RoleType.SUPERADMIN_ROLE));
        signupUser.getRoleHashSet().add(assignedRole);

        log.info("User Details: {}", signupUser);
        userRepository.save(signupUser);
        return getSignUpUserResponse(signupUser);
    }

    @Override
    @Transactional
    public SignUpUserResponse signUp(SignUpRequest signUpRequest) {

        RoleType requestedRoleType = signUpRequest.getRoleType() != null ? signUpRequest.getRoleType() : RoleType.USER_ROLE;

        log.info("Requested RoleType: {}", requestedRoleType);

        if (requestedRoleType != RoleType.USER_ROLE) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnAuthorizedException("Only SUPERADMIN can create accounts for roles other than USER_ROLE!");
            }


            User currentUser = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(() -> new UnAuthorizedException("Unauthorized"));

            boolean isSuperAdmin = currentUser.getRoleHashSet().stream()
                    .anyMatch(role -> role.getRoleType() == RoleType.SUPERADMIN_ROLE);

            if (!isSuperAdmin) {
                throw new UnAuthorizedException("Only SUPERADMIN can assign roles other than USER_ROLE!");
            }
        }

        User signupUser = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .DateOfBirth(signUpRequest.getDateOfBirth())
                .identificationType(signUpRequest.getIdentificationType())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .idNumber(signUpRequest.getIdNumber())
                .confirmPassword(bCryptPasswordEncoder.encode(signUpRequest.getConfirmPassword()))
                .roleHashSet(new HashSet<>())
                .build();
        Role assignedRole = roleService.findByRoleType(requestedRoleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + requestedRoleType));
        signupUser.getRoleHashSet().add(assignedRole);

        log.info("User Roles Before Saving: {}", signupUser.getRoleHashSet()); // Debugging
        userRepository.save(signupUser);
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
                .password(signupUser.getPassword())
                .confirmPassword(signupUser.getConfirmPassword())
                .idNumber(signupUser.getIdNumber())
                .phoneNumber(signupUser.getPhoneNumber())
                .message("User signed up successfully")
                .roles(signupUser.getRoleHashSet())
                .build();

    }


    private void validateUserInfo(User user) {
        if (!Objects.equals(user.getPassword(), user.getConfirmPassword())) {
            throw new PasswordDoesNotMatchException("The passwords you entered do not match. Please ensure both fields are identical.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistException("User with the email already Exist");
        }
        if (user.getIdNumber() == null || user.getIdNumber().length() < 10 || user.getIdNumber().length() > 15) {
            throw new InvalidIdNumber("IDss number must be between 10 and 15 characters.");
        }
        if (userRepository.existsByIdNumber(user.getIdNumber())) {
            throw new IdNumberAlreadyExist("user identification number already exist");

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
        return user.orElse(null); // Return null if user is not found
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


    private UserLoginResponse buildSuccessfulLoginResponse(User user) {
        return UserLoginResponse.builder()
                .code(200)
                .message("Login successful")
                .build();

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





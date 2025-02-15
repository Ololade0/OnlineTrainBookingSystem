package train.booking.train.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.repository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    @Override
    public SignUpUserResponse signUp(SignUpRequest signUpRequest) {

        RoleType requestedRoleType = signUpRequest.getRoleType() != null ? signUpRequest.getRoleType() : RoleType.USER_ROLE;
        if (requestedRoleType != RoleType.USER_ROLE && requestedRoleType != RoleType.SUPERADMIN_ROLE) {
            throw new UnAuthorizedException("Only SUPERADMIN can create accounts for roles other than USER_ROLE!");
        }

        User signupUser = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .DateOfBirth(signUpRequest.getDateOfBirth())
                .identificationType(signUpRequest.getIdentificationType())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(signUpRequest.getPassword())
                .idNumber(signUpRequest.getIdNumber())
                .confirmPassword(signUpRequest.getConfirmPassword())
                .roleHashSet(new HashSet<>())
                .build();

        Role assignedRole = roleService.findByRoleType(requestedRoleType)
                .orElseGet(() -> roleService.save(new Role(RoleType.USER_ROLE)));
        signupUser.getRoleHashSet().add(assignedRole);

        log.info("User Details: {}", signupUser);
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


    private void validateUserInfo(User user){
        if(!Objects.equals(user.getPassword(), user.getConfirmPassword())){
            throw new PasswordDoesNotMatchException("The passwords you entered do not match. Please ensure both fields are identical.");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistException("User with the email already Exist");
        }
        if(user.getIdNumber() == null || user.getIdNumber().length() < 10 || user.getIdNumber().length() > 15){
            throw new InvalidIdNumber("IDss number must be between 10 and 15 characters.");

        }
        if(userRepository.existsByIdNumber(user.getIdNumber())){
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
    public User save(User user) {
       return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

}






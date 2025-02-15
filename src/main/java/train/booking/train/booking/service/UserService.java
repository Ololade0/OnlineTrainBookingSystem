package train.booking.train.booking.service;

import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;

import java.util.Optional;

public interface UserService {
   SignUpUserResponse signUp(SignUpRequest signUpRequest);
   User findUserByEmail(String email);
   User findUserById(Long userId);

   User findUserByEmailOrNull(String email);

   User save(User user);

    Optional<User> getUserById(Long userId);
}

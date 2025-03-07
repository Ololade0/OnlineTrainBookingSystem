package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.request.UserLoginRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.model.User;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

public interface UserService extends UserDetailsService {
   SignUpUserResponse signUp(SignUpRequest signUpRequest) throws UnirestException, RoleNotFoundException;
   SignUpUserResponse superAdminSignUp(SignUpRequest signUpRequest) throws UnirestException;
   User findUserByEmail(String email);
   User findUserById(Long userId);

   User findUserByEmailOrNull(String email);


    Optional<User> getUserById(Long userId);

   UserLoginResponse login(UserLoginRequest userLoginRequestModel);

   void disableUser(String email);

   void deleteUser(String email);

   Optional<User> findUserByActivationToken(String token);

   String activateAccount(String token) throws UnirestException;


   void enableUser(String email);
}

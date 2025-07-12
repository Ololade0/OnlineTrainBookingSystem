package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.BaseResponse;

import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.model.User;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

public interface UserService extends UserDetailsService {
   BaseResponse superAdminSignUp(UserDTO userDTO) throws UnirestException, RoleNotFoundException;
    BaseResponse signUpNewUser(UserDTO userDTO) throws UnirestException, RoleNotFoundException;
   BaseResponse findUserByEmail(String email);

   User findUserByEmailOrNull(String email);
   User findUserById(Long userId);

   UserLoginResponse login(UserLoginDTO userLoginRequestModel);

   Optional<User> findUserByActivationToken(String token);

   BaseResponse activateAccount(String token) throws UnirestException;

   BaseResponse updateUserProfile(UserDTO userDTO, Long userId);

    void save(User user);
}

package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.model.User;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    BaseResponse signUpNewUser(UserDTO userDTO) throws UnirestException, RoleNotFoundException;
    User findUserByEmail(String email);
    User findUserByEmailOrNull(String email);
    User findUserById(Long userId);

//    UserLoginResponse login(UserLoginDTO userLoginRequestModel);

    Optional<User> findUserByActivationToken(String token);

    String activateAccount(String token) throws UnirestException;

    BaseResponse updateUserProfile(UserDTO userDTO, Long userId);

    void save(User user);


    Page<User> getAllNonUserAccounts(int page, int size);

    Page<User> searchUsers(String query, int page, int size);

    String deleteUser(Long userId);
}

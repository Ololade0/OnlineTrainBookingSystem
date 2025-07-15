package train.booking.train.booking.service;

import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.model.AuthToken;

public interface AuthTokenService {
    UserLoginResponse login(UserLoginDTO userLoginRequestModel);

    void logout(String token);

    void saveToken(  AuthToken authToken);
}

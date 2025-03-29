package train.booking.train.booking.service;

import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.UserLoginResponse;

public interface AuthTokenService {
    UserLoginResponse login(UserLoginDTO userLoginRequestModel);
}

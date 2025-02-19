package train.booking.train.booking.service;

import train.booking.train.booking.dto.request.UserLoginRequest;
import train.booking.train.booking.dto.response.UserLoginResponse;

public interface AuthTokenService {
    UserLoginResponse login(UserLoginRequest userLoginRequestModel);
}

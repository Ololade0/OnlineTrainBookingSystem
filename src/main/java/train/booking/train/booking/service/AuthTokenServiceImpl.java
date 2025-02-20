package train.booking.train.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.request.UserLoginRequest;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthTokenServiceImpl implements AuthTokenService{

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequestModel) {
        var user = userService.findUserByEmail(userLoginRequestModel.getEmail());
        if (user != null && bCryptPasswordEncoder.matches(userLoginRequestModel.getPassword(), user.getPassword())) {
            return buildSuccessfulLoginResponse(user);
        }
        throw new IllegalArgumentException("Invalid email or password");


    }

    private UserLoginResponse buildSuccessfulLoginResponse(User user) {
        return UserLoginResponse.builder()
                .code(200)
                .message("Login successful")
                .user(user)
                .build();

    }



}

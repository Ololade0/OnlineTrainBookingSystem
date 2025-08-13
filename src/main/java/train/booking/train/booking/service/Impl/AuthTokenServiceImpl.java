package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.exceptions.AlreadyLoggoutTokenException;
import train.booking.train.booking.model.AuthToken;
import train.booking.train.booking.model.User;
import train.booking.train.booking.repository.AuthTokenRepository;
import train.booking.train.booking.service.AuthTokenService;
import train.booking.train.booking.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthTokenServiceImpl implements AuthTokenService {

    private final UserService userService;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthTokenRepository authTokenRepository;

    @Override
    public UserLoginResponse login(UserLoginDTO userLoginRequestModel) {
        User user = userService.findUserByEmailOrNull(userLoginRequestModel.getEmail());
        if (user != null && userLoginRequestModel.getPassword().equals(user.getPassword())) {
            return buildSuccessfulLoginResponse(user);
        }
        throw new IllegalArgumentException("Invalid email or password");


    }

    @Override
    @Transactional
    public void logout(String token) {
        Optional<AuthToken> optionalAuthToken = authTokenRepository.findByToken(token);
        if (optionalAuthToken.isPresent()) {
            authTokenRepository.deleteByToken(token);
        } else {
            throw new AlreadyLoggoutTokenException("Already Loggout token");
        }

    }

    @Override
    public void saveToken(AuthToken token) {
        authTokenRepository.save(token);
    }

    private UserLoginResponse buildSuccessfulLoginResponse(User user) {
        return UserLoginResponse.builder()
                .code(200)
                .message("Login successful")
                .email(user.getEmail())
                .build();

    }



}

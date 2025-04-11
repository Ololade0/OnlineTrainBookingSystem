package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.exceptions.UserCannotBeFoundException;
import train.booking.train.booking.model.AuthToken;
import train.booking.train.booking.model.User;
import train.booking.train.booking.security.jwt.TokenProvider;
import train.booking.train.booking.service.AuthTokenService;
import train.booking.train.booking.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) throws UserCannotBeFoundException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword())
        );
        authTokenService.login(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateJWTToken(authentication);
        User user = userService.findUserByEmailOrNull(loginRequest.getEmail());
        return new ResponseEntity<>(new AuthToken(token, user.getFirstName(), user.getEmail()), HttpStatus.OK);
    }
}


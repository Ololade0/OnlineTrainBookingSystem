package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.dto.response.UserLoginResponse;
import train.booking.train.booking.exceptions.UserCannotBeFoundException;
import train.booking.train.booking.model.AuthToken;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.AuthTokenStatus;
import train.booking.train.booking.security.jwt.TokenProvider;
import train.booking.train.booking.service.AuthTokenService;
import train.booking.train.booking.service.UserService;

import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;


    // AuthController.java
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findUserByEmail(loginRequest.getEmail());
            if (user.isVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found"));
            }

            String jwtToken = tokenProvider.generateJWTToken(authentication);
            Instant expiryDate = Instant.now().plusSeconds(3600);

            AuthToken authToken = AuthToken.builder()
                    .token(jwtToken)
                    .name(user.getFirstName())
                    .email(user.getEmail())
                    .expiryDate(expiryDate)
                    .authTokenStatus(AuthTokenStatus.ACTIVE)
                    .build();

            authTokenService.saveToken(authToken);

            return ResponseEntity.ok(Map.of(
                    "token", jwtToken,
                    "firstName", user.getFirstName(),
                    "email", user.getEmail()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred. Please try again."));
        }
    }


//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) throws UserCannotBeFoundException {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
//                        loginRequest.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwtToken = tokenProvider.generateJWTToken(authentication);
//        User user = userService.findUserByEmailOrNull(loginRequest.getEmail());
//        Instant expiryDate = Instant.now().plusSeconds(3600);
//        AuthToken authToken = AuthToken.builder()
//                .token(jwtToken)
//                .name(user.getFirstName())
//                .email(user.getEmail())
//                .expiryDate(expiryDate)
//                .authTokenStatus(AuthTokenStatus.ACTIVE)
//                .build();
//        authTokenService.saveToken(authToken);
//        authTokenService.login(loginRequest);
//
//
//        return new ResponseEntity<>(new UserLoginResponse(jwtToken, user.getFirstName(), user.getEmail()), HttpStatus.OK);
//    }



    @PostMapping("/logout")
    public void logout(@RequestParam String token) {
        authTokenService.logout(token);

    }

}





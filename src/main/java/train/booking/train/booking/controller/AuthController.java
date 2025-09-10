package train.booking.train.booking.controller;





import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.UserLoginDTO;
import train.booking.train.booking.model.User;
import train.booking.train.booking.security.jwt.TokenProvider;
import train.booking.train.booking.service.AuthTokenService;
import train.booking.train.booking.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

    @RestController
    @RequestMapping("/api/v1/auth")
    @RequiredArgsConstructor
    public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final TokenProvider tokenProvider;
        private final UserService userService;
        private final PasswordEncoder passwordEncoder;
        private final AuthTokenService authTokenService;

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                    || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "missing_credentials",
                                "message", "Email and password are required."
                        ));
            }

            Optional<User> optionalUser = Optional.ofNullable(userService.findUserByEmail(loginRequest.getEmail()));
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "user_not_found",
                                "message", "No account exists with this email."
                        ));
            }

            User user = optionalUser.get();
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "invalid_password",
                                "message", "Incorrect password."
                        ));
            }
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwtToken = tokenProvider.generateJWTToken(authentication);

                return ResponseEntity.ok(Map.of(
                        "accessToken", jwtToken,
                        "email", user.getEmail(),
                        "roles", user.getRoleHashSet()
                                .stream()
                                .map(role -> role.getRoleType().name())
                                .toList()
                ));

            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "invalid_credentials",
                                "message", "Invalid email or password."
                        ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "error", "server_error",
                                "message", "An error occurred. Please try again."
                        ));
            }
        }


        @PostMapping("/logout")
            public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
                authTokenService.logout(token);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Successfully logged out");
                return ResponseEntity.ok(response);
            }


    }








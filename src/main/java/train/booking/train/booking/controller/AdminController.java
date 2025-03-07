package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.service.UserService;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;


    @PostMapping("/register-superadmin")
    public ResponseEntity<?> superAdminSignUp(@Valid @RequestBody SignUpRequest signUpRequest) throws UnirestException {
        SignUpUserResponse registeredUser = userService.superAdminSignUp(signUpRequest);
        log.info("Incoming user payload: {}", registeredUser);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/disable")
    public ResponseEntity<String> disableUser(@RequestParam String email){
        userService.disableUser(email);
        return ResponseEntity.ok("User disabled");
    }


    @PutMapping("/users/enable")
    public ResponseEntity<String> enableUser(@RequestParam String email) {
        userService.enableUser(email);
        return ResponseEntity.ok("User enabled successfully");
    }
}

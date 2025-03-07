package train.booking.train.booking.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.exceptions.UserCannotBeFoundException;
import train.booking.train.booking.model.User;
import train.booking.train.booking.repository.UserRepository;
import train.booking.train.booking.security.jwt.TokenProvider;
import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid  @RequestBody SignUpRequest signUpRequest) throws UnirestException, RoleNotFoundException {
        SignUpUserResponse registeredUser = userService.signUp(signUpRequest);
        log.info("Incoming user payload: {}", registeredUser);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }



    @GetMapping("/find-user/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable("email") String email) {
        try {
            User foundUserByEmail = userService.findUserByEmail(email);
            return new ResponseEntity<>(foundUserByEmail, HttpStatus.OK);
        } catch (UserCannotBeFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        userService.deleteUser(email);
        return ResponseEntity.ok("User account deleted");
    }

}

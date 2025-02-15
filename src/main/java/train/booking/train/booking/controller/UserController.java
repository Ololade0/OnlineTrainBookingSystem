package train.booking.train.booking.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.exceptions.UserCannotBeFoundException;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.service.UserService;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid  @RequestBody SignUpRequest signUpRequest){
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

}

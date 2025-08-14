package train.booking.train.booking.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {
        return new ResponseEntity<>(userService.signUpNewUser(signUpRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        BaseResponse foundUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(foundUser);
    }

    @PutMapping("update-user-profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        ;
        return ResponseEntity.ok(userService.updateUserProfile(userDTO, userId));

    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        try {
            String result = userService.activateAccount(token);
            return ResponseEntity.ok(result);
        } catch (RuntimeException | UnirestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }

}





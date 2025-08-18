package train.booking.train.booking.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.User;
import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {
        return new ResponseEntity<>(userService.signUpNewUser(signUpRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        User foundUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(foundUser);
    }

    @PutMapping("update-user-profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        ;
        return ResponseEntity.ok(userService.updateUserProfile(userDTO, userId));

    }


    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, Model model) {
        try {
            String result = userService.activateAccount(token);
            model.addAttribute("message", result);
            return "activation-success";
        } catch (RuntimeException | UnirestException e) {
            model.addAttribute("message", e.getMessage());
            return "activation-failed";
        }
    }


}





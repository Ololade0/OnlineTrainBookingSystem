package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import train.booking.train.booking.dto.UserDTO;

import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;


    @PostMapping("/register-superadmin")
    public ResponseEntity<?> superAdminSignUp(@Valid @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {;
        return new ResponseEntity<>(userService.superAdminSignUp(signUpRequest), HttpStatus.CREATED);
    }





}

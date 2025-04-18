package train.booking.train.booking.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.repository.UserRepository;
import train.booking.train.booking.security.jwt.TokenProvider;
import train.booking.train.booking.service.PriceListService;
import train.booking.train.booking.service.SeatService;
import train.booking.train.booking.service.TrainService;
import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final SeatService seatService;
    private final TrainService trainService;
    private final PriceListService priceListService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid  @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {
        return new ResponseEntity<>(userService.signUp(signUpRequest), HttpStatus.CREATED);
    }
    @PostMapping("/{email}")
    public ResponseEntity<?> registerUser(@PathVariable  String email){
       BaseResponse foundUser = userService.findUserByEmail(email);
        return  ResponseEntity.ok(foundUser);
    }







}

package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.UserService;


@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token) throws UnirestException {
        String foundUser = userService.activateAccount(token);
        return ResponseEntity.ok(foundUser);
    }



}

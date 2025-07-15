package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookSeatDTO;
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
    public ResponseEntity<?> activateAccount(@PathVariable String token) throws UnirestException {
        return ResponseEntity.ok(userService.activateAccount(token));
    }

    @PostMapping("/trigger-websocket")
    public ResponseEntity<String> triggerWebSocket(@RequestBody BookSeatDTO seatDto) {
        notificationService.webSocketNotification(seatDto);
        return ResponseEntity.ok("WebSocket seat update sent");
    }
    }

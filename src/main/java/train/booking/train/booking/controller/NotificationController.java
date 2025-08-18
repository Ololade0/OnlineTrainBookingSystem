package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.UserService;


@RestController
@Slf4j
@RequestMapping("/api/v1/auth/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;


//            @GetMapping("/activate")
//            public String activateAccount(@RequestParam("token") String token, Model model) {
//                try {
//                    String result = userService.activateAccount(token);
//                    model.addAttribute("message", result);
//                    return "activation-success";
//                } catch (RuntimeException ex) {
//                    model.addAttribute("message", ex.getMessage());
//                    return "activation-failed";
//                } catch (Exception ex) {
//                    model.addAttribute("message", "An unexpected error occurred during activation.");
//                    return "activation-failed";
//                }
//            }
//


    @PostMapping("/trigger-websocket")
    public ResponseEntity<String> triggerWebSocket(@RequestBody BookSeatDTO seatDto) {
        notificationService.webSocketNotification(seatDto);
        return ResponseEntity.ok("WebSocket seat update sent");
    }
    }

package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.service.UserService;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN_ROLE')")
public class AdminController {

    private final UserService userService;

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

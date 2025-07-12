package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.ResetPasswordDto;
import train.booking.train.booking.service.PasswordResetTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth/")
public class PasswordResetController {

    private final PasswordResetTokenService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws UnirestException {
      String forgotPassword =  passwordResetService.forgotPassword(email);
        return new ResponseEntity<>(forgotPassword, HttpStatus.OK);
    }

    @GetMapping("/reset-password/validate")
    public void  validateToken(@RequestParam String token) {
        passwordResetService.validatePasswordResetToken(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody  ResetPasswordDto resetPasswordDto) {
       String password =  passwordResetService.resetPassword(resetPasswordDto);
        return new ResponseEntity<>(password, HttpStatus.OK);
    }
}

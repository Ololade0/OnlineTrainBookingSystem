package train.booking.train.booking.service.Impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.ResetPasswordDto;
import train.booking.train.booking.exceptions.PasswordException;
import train.booking.train.booking.model.PasswordResetToken;
import train.booking.train.booking.model.User;
import train.booking.train.booking.repository.PasswordResetTokenRepository;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.PasswordResetTokenService;
import train.booking.train.booking.service.UserService;
import train.booking.train.booking.utils.Helper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl  implements PasswordResetTokenService {

    @Value("${activation.url}")
    private String resetPasswordUrl;


    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Helper helper;


    @Override
    public String forgotPassword(String email) throws UnirestException {
        User foundUser =   userService.findUserByEmailOrNull(email);
        if(foundUser != null) {
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
            String token = UUID.randomUUID().toString();
            passwordResetTokenRepository.deleteByUserId(foundUser.getId());

            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .expiryDate(expiryDate)
                    .token(token)
                    .userId(foundUser.getId())
                    .build();
            passwordResetTokenRepository.save(passwordResetToken);
            Map m = new HashMap<>();
            m.put("resetPasswordUrl", resetPasswordUrl + "?token=" + token);
            m.put("customerName", foundUser.getFirstName());
            notificationService.sendEmailV3(foundUser.getEmail(), "ACTIVATION LINK", helper.build(m, "register-admin"));
            return "Reset Link has been sent to your email";

        }
        throw new PasswordException("Credentials cannot be found");

    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired");
        }

    }


    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetPasswordDto.getToken())
                .orElseThrow(() -> new PasswordException("Invalid token"));
        User foundUser = userService.findUserById(resetToken.getUserId());
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new PasswordException("Token has expired");
        }
        foundUser.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getNewPassword()));
        userService.save(foundUser);
        passwordResetTokenRepository.delete(resetToken);
        return "Password reset successfully";
    }
}



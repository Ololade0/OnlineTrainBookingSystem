package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import train.booking.train.booking.dto.ResetPasswordDto;

public interface PasswordResetTokenService{

String forgotPassword(String email) throws UnirestException;
    void validatePasswordResetToken(String token);
    String resetPassword(ResetPasswordDto resetPasswordDto);

}

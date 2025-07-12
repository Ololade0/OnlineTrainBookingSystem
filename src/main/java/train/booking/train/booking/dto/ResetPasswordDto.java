package train.booking.train.booking.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordDto {
//    private Long userId;
    private String email;
    private String token;
    private String newPassword;

}

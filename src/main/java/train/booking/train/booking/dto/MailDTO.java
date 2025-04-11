package train.booking.train.booking.dto;

import jakarta.validation.constraints.Email;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDTO {
    @Email
    private String sender;
    @Email
    private String receiver;
    private String subject;
    private String body;
    private boolean isSuccessful;


    public MailDTO(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}

package train.booking.train.booking.dto;

import jakarta.validation.constraints.Email;
import lombok.*;


@Getter
@Setter
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

    public MailDTO(String sender, String receiver, String subject, String body, boolean isSuccessful) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.isSuccessful = isSuccessful;
    }

    public MailDTO(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}

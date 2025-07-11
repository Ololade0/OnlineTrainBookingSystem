package train.booking.train.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
    public class PaymentVerificationResponse {
        private boolean success;
        private String transactionId;
        private String status;

}

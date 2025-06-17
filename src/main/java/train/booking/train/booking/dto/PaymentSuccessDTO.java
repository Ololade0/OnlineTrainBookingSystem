package train.booking.train.booking.dto;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessDTO {

    private Long bookingId;
    private String paymentId;
}

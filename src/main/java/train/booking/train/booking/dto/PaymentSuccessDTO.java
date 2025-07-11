package train.booking.train.booking.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessDTO {

    private Long bookingId;
    private Long scheduleId;
    private String paymentId;
    private String pnrCode;
    private String email;
    private BigDecimal totalAmount;
}

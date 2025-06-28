package train.booking.train.booking.dto;

import lombok.*;

import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private BigDecimal totalFare;
    private Long userId;
    private String email;
    private Long bookingId;
//    private Currency currency;
    private String description;
    private String cancelUrl;
    private String successUrl;
    private String intent;
    private String pnrCode;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;



}

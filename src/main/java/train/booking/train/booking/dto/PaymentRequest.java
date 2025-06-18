package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.enums.Currency;
import train.booking.train.booking.model.enums.PaymentMethod;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private Double totalFare;
    private Long userId;
    private Long bookingId;
    private Currency currency;
    private String description;
    private String cancelUrl;
    private String successUrl;
    private String intent;
    private String pnrCode;

    private PaymentMethod paymentMethod;



}

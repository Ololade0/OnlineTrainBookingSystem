package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@Entity(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class BookingPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalPrice;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionReference;
    private LocalDateTime paymentDate;
    private String currency;
//    private String intent;
//    private String description;
    private String cancelUrl;
    private String successUrl;




    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}



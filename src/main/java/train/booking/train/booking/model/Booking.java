

package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Builder
@Entity(name = "bookings")
@AllArgsConstructor
public class Booking implements Serializable {
    private static final long  serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private LocalDateTime bookingDate;
    private String bookingNumber;
    private LocalDate travelDate;
    private LocalTime travelTime;
    private BigDecimal totalFareAmount;
    @Enumerated(EnumType.STRING)
    private AgeRange passengerType;
    private int seatNumber;
//    @Column(name = "expiration_time")
//    private LocalDateTime expirationTime;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user ;



    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;


    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 20)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method",  length = 20)
    private PaymentMethod paymentMethod;


//    @Enumerated(EnumType.STRING)
//    @Column(name = "seat_status", length = 20)
//    private SeatStatus seatStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_Status",  length = 20)
    private PaymentStatus paymentStatus;


    private Long scheduleId;

    public Booking() {

    }
}

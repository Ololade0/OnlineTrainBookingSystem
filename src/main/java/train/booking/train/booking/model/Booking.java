

package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.TrainClass;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@Entity(name = "bookings")
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private LocalDateTime bookingDate;
    private String bookingNumber;
    private String passengerNameRecord;
    private LocalDateTime travelDate;
    private Double totalFareAmount;
    private String passengerType;
    private int seatNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;


    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 20)
    private BookingStatus bookingStatus;

    private Long scheduleId;

}

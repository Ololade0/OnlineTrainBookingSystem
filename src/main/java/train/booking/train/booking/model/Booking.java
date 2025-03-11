

package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
//@ToString(exclude = {"seats", "BookingPayment"})
@Entity(name = "bookings")
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private LocalDateTime bookingDate;
    private String passengerNameRecord;
    private LocalDateTime travelDate;
    private Double totalFareAmount;
    private String passengerType;
    private String approvalUrl;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;


    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

}

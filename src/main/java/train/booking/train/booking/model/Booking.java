

package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;
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

    private Long scheduleId;

}

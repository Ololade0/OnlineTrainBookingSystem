
package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.model.enums.TrainClass;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@Entity(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int seatNumber;
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    private LocalDateTime lockTime;


    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking ;
    private Long scheduleId;


}
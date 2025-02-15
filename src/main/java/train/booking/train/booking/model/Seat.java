package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.SeatStatus;

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
//    private int totalSeat;
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "train_class_id")
    private TrainClass trainClass;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

}



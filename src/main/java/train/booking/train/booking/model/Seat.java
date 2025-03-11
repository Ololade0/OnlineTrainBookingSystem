
package train.booking.train.booking.model;

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
    @Enumerated(EnumType.STRING)
    private SeatStatus status;



    private String name;


    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;


}
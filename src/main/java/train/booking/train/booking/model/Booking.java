

package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@Entity(name = "bookings")
@AllArgsConstructor
public class Booking {
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
    private AgeRange ageRange;
    private int seatNumber;



    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user ;



    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;


    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 20)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method",  length = 20)
    private PaymentMethod paymentMethod;


    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private BookingPayment bookingPayment;

    @OneToMany(mappedBy = "booking")
    @JsonManagedReference
    private List<OtherPassenger> otherPassengers = new ArrayList<>();

    private Long scheduleId;

    public Booking() {

    }



}

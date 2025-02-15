package train.booking.train.booking.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@ToString(exclude = {"seats", "BookingPayment"})

@Entity(name = "bookings")
@AllArgsConstructor

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    private LocalDateTime bookingDate;

    private String PassengerNameRecord;

    private LocalDateTime travelDate;
    private Double totalFareAmount;

    private String passengerType;
    private int seatNumber;

    private String approvalUrl;
    @JsonBackReference("user-booking")
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JsonBackReference("trainClass-booking")
    @ManyToOne
    @JoinColumn(name = "train_class_id")
    private TrainClass trainClass;

    @JsonManagedReference("booking-seat")
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BookingPayment bookingPayment;

    @JsonBackReference("schedule-booking")
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 20)
    private BookingStatus bookingStatus;



    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OtherPassenger> otherPassengers = new ArrayList<>();

    public Booking() {
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        if (this.seats == null) {
            this.seats = new ArrayList<>();
        }
        this.seats.add(seat);
    }

    public void updateStatus(BookingStatus newStatus) {
        this.bookingStatus = newStatus;
    }



}

package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.model.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class BookingTicketDTO {
    private Long bookingId;
    private String trainName;
    private String trainCode;
    private String mapBookingTicketDTO;
    private LocalDate travelDate;
    private String bookingNumber;
    private BookingStatus bookingStatus;
    private String sourceStation;
    private String destinationStation;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private TrainClass trainClass;
    private PaymentMethod paymentMethod;
    private BigDecimal totalFare;
    private String firstName;
    private AgeRange ageRange;
    private int seatNumber;
    private IdentificationType identificationType;
    private String idNumber;
    private List<OtherPassenger> otherPassengers;

}
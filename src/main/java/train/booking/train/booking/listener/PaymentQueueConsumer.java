package train.booking.train.booking.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.service.*;
import train.booking.train.booking.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueConsumer {

    @Value("${qrbase.Url}")
    private String qrbaseUrl;

    private final BookingService bookingService;
    private final SeatService seatService;
    private final OtherPassengerService otherPassengerService;


    private final PaymentService paymentService;

    private final NotificationService notificationService;
    private final Helper helper;


    private final ObjectMapper mapper = new ObjectMapper();


    @Transactional
    @JmsListener(destination = "payment-queue")
    public void handlePaymentSuccess(String json) throws Exception {
        PaymentSuccessDTO dto = mapper.readValue(json, PaymentSuccessDTO.class);
        Booking booking = bookingService.findBookingById(dto.getBookingId());

        if (booking.getBookingStatus() == BookingStatus.BOOKED) {
            log.info("Booking {} already processed", booking.getBookingNumber());
            return;
        }

        // Update Booking Status
        bookingService.updateBookingStatus(dto.getBookingId());

        // Book main user seat
        BookSeatDTO seatDto = new BookSeatDTO();
        seatDto.setTrainClass(booking.getTrainClass());
        seatDto.setSeatNumber(booking.getSeatNumber());
        seatDto.setBookingId(booking.getBookingId());
        Seat seat = seatService.bookSeat(seatDto);


        log.info("Seat {} booked for {}", seat.getSeatNumber(), booking.getBookingNumber());
        seatDto.setAvailable(false);
        notificationService.webSocketNotification(seatDto);


        // Book seats for other passengers
        otherPassengerService.bookSeatForOtherPassengers(dto, booking);


        // Finally, mark the payment as COMPLETED
        BookingPayment bookingPayment =   paymentService.updateBookingPayment(dto.getPaymentId());
       BookingTicketDTO generateReceipt =  bookingService.generateBookingReceipt(booking.getBookingId());
        Map map = mapGenerateReceipt(generateReceipt);
        map.put("QRCode", bookingService.generateQRCodeBase64(qrbaseUrl + generateReceipt.getBookingNumber()));
        notificationService.sendBookingReceipts(bookingPayment.getUser().getEmail(),"BOOKING RECEIPT", helper.build(map, "booking-receipt"));
            log.info("Marked payment {} as COMPLETED for booking {}", dto.getPaymentId(), dto.getBookingId());
        }


    private static Map<String, Object> mapGenerateReceipt(BookingTicketDTO generateReceipt) {
        Map<String, Object> map = new HashMap<>();
        map.put("FirstName", generateReceipt.getFirstName()); // ✅ Matches ${FirstName}
        map.put("Message", "Thank you for using the Nigerian Railway Corporation online ticket booking services. Your ticket booking details are as follows:");
        map.put("TrainName", generateReceipt.getTrainName()); // ✅ Matches ${TrainName}
        map.put("TrainCode", generateReceipt.getTrainCode());
        map.put("PNRCode", generateReceipt.getBookingNumber()); // ✅ Matches ${PNRCode}
        map.put("BookingStatus", generateReceipt.getBookingStatus());
        map.put("TravelDate", generateReceipt.getTravelDate());
        map.put("DepartureTime", generateReceipt.getDepartureTime());
        map.put("ArrivalTime", generateReceipt.getArrivalTime());
        map.put("SourceStation", generateReceipt.getSourceStation());
        map.put("DestinationStation", generateReceipt.getDestinationStation());
        map.put("Class", generateReceipt.getTrainClass());
        map.put("SeatNumber", generateReceipt.getSeatNumber());
        map.put("TotalFare", generateReceipt.getTotalFare());
        map.put("PaymentMethod", generateReceipt.getPaymentMethod());
        map.put("AgeRange", generateReceipt.getAgeRange());
        map.put("IdentificationType", generateReceipt.getIdentificationType());
        map.put("IDNumber", generateReceipt.getIdNumber());

        // Optional: add passenger details if list is not empty
        if (generateReceipt.getOtherPassengers() != null && !generateReceipt.getOtherPassengers().isEmpty()) {
            List<Map<String, Object>> passengerList = new ArrayList<>();
            for (OtherPassenger p : generateReceipt.getOtherPassengers()) {
                Map<String, Object> passengerInfo = new HashMap<>();
                passengerInfo.put("Name", p.getName());
                passengerInfo.put("Email", p.getEmail());
                passengerInfo.put("IDNumber", p.getIdNumber());
                passengerInfo.put("PassengerType", p.getPassengerType());
                passengerInfo.put("SeatNumber", p.getSeatNumber());
                passengerInfo.put("IdentificationType", p.getIdentificationType());
                passengerList.add(passengerInfo);
            }
            map.put("OtherPassengers", passengerList);

        }

        log.error("GENERATED RECEIPT: {}", map);
        return map;
    }



    @JmsListener(destination = "payment-failure-queue")
    public void handlePaymentFailure(String json) {
        log.warn("Received failed payment details for analysis: {}", json);
        // Optionally store or alert devops team
    }
}

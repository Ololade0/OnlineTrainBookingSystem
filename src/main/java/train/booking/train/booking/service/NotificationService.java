package train.booking.train.booking.service;


import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingTicketDTO;

public interface NotificationService {
    void sendBookingReceipts(String recipient, String subject, BookingTicketDTO bookingTicketDTO);
        void webSocketNotification(BookSeatDTO seatDto);

    void sendEmailV3(String recipient, String subject, String body);
}

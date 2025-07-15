package train.booking.train.booking.service;


import train.booking.train.booking.dto.BookSeatDTO;

public interface NotificationService {

   void sendBookingReceipts(String recipient, String subject, String body);
        void webSocketNotification(BookSeatDTO seatDto);

    void sendEmailV3(String recipient, String subject, String body);
}

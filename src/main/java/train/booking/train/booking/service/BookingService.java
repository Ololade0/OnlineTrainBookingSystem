package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.enums.BookingStatus;

public interface BookingService {
   void createBooking(BookingRequestDTO bookingDTO);

   Booking findBookingById(Long bookingId);

   Booking saveBooking(Booking savedBooking);

   boolean existsByBookingNameRecord(String passengerNameRecord);

   Booking findByTransactionId(String transactionId);

   Booking updateBookingStatus(Long bookingId, BookingStatus booked);


//   void updateBookingStatus(Booking booking);


//   void sendPaymentRequest(PaymentRequest paymentRequestDTO);
//   public Booking bookingWithPayment(Long bookingId, PaymentRequest paymentRequest);

}

package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.model.Booking;

public interface BookingService {
   void createBooking(BookingRequestDTO bookingDTO);
   Booking saveBooking(BookingQueueDTO dto);

   Booking findBookingById(Long bookingId);
   boolean existsByBookingNameRecord(String passengerNameRecord);

   Booking findByTransactionId(String transactionId);


}

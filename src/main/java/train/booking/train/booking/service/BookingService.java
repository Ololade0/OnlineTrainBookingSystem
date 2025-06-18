package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.model.Booking;

import java.util.Optional;

public interface BookingService {
   BookingResponse createBooking(BookingRequestDTO bookingDTO);
   Booking saveBooking(BookingQueueDTO dto);
   Optional<Booking> findBookingByBookingNumber(String bookingNumber);

   Booking findBookingById(Long bookingId);
//   boolean existsByBookingNameRecord(String passengerNameRecord);

   Booking findByTransactionId(String transactionId);


   Booking updateBookingStatus(Long bookingId);

   Booking updateBooking(Booking booking);
}

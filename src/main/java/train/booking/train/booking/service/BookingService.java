package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.model.Booking;

import java.util.Optional;

public interface BookingService {
   BookingResponse createBooking(BookingRequestDTO bookingDTO);
   Booking saveBooking(BookingQueueDTO dto);
   Optional<Booking> findBookingByBookingNumber(String bookingNumber);

   BookingTicketDTO generateBookingReceipt(Long bookingId);

   byte[] downloadBookingReceipt(Long bookingId) throws Exception;

   Booking findBookingById(Long bookingId);

   Booking updateBookingStatus(Long bookingId);

   BookingTicketDTO scanQRBookingCode(String bookingNumber);
}

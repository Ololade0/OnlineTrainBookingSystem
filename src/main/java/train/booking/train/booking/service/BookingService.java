package train.booking.train.booking.service;

import com.google.zxing.WriterException;
import com.mashape.unirest.http.exceptions.UnirestException;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.model.Booking;

import java.io.IOException;
import java.util.Optional;

public interface BookingService {
   BookingResponse createBooking(BookingRequestDTO bookingDTO);
   Booking saveBooking(BookingQueueDTO dto);
   Optional<Booking> findBookingByBookingNumber(String bookingNumber);

   BookingTicketDTO generateBookingReceipt(Long bookingId) throws UnirestException;


 byte[] generateReceiptPdf(Long bookingId) throws Exception;

    String generateQRCodeBase64(String text) throws WriterException, IOException;

   Booking findBookingById(Long bookingId);

   Booking updateBookingStatus(Long bookingId);

   BookingTicketDTO scanQRBookingCode(String bookingNumber);
}

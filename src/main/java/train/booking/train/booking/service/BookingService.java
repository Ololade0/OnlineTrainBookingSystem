package train.booking.train.booking.service;

import com.google.zxing.WriterException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.TrainClass;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingResponse createBooking(BookingRequestDTO bookingDTO);

    Booking saveBooking(BookingQueueDTO dto);

    Optional<Booking> findBookingByBookingNumber(String bookingNumber);

    BookingTicketDTO generateBookingReceipt(Long bookingId) throws UnirestException;


    byte[] generateReceiptInPdf(Long bookingId) throws Exception;

    String generateQRCodeBase64(String text) throws WriterException, IOException;

    Booking findBookingById(Long bookingId);

    Booking updateBookingStatus(Long bookingId);

    BookingTicketDTO scanQRBookingCode(String bookingNumber);

    Page<Booking> findAllBookingsBySchedule(int size, int page, Long scheduleId);

    Page<Booking> bookingHistory(Long userId, int page, int size);
    Page<Booking> getAllBookings(BookingStatus bookingStatus, AgeRange ageRange,  TrainClass trainClass,
                                 PaymentMethod paymentMethod, LocalDate localDate, int page, int size);


    List<BookingStatus> findAllBookingStatus();
}

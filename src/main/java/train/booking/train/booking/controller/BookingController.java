package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.service.BookingService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth/booking")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponse bookingResponse = bookingService.createBooking(bookingRequestDTO);
        return ResponseEntity.ok(bookingResponse);

    }

    @GetMapping("/pnr/{pnrCode}")
    public ResponseEntity<?> findBookingByPnr(@PathVariable String pnrCode) {
        Optional<Booking> booking = bookingService.findBookingByBookingNumber(pnrCode);
        return ResponseEntity.ok(booking);
    }


    @GetMapping("/receipt/{bookingId}")

    public  ResponseEntity<?> findBookingReceipt(@PathVariable  Long bookingId) throws IOException, UnirestException {
        BookingTicketDTO booking =bookingService.generateBookingReceipt(bookingId);
        return ResponseEntity.ok(booking);

    }

    @GetMapping("scan/qr/{bookingNumber}")
    public ResponseEntity<?> scanQRBookingCode(@PathVariable String bookingNumber) {
        BookingTicketDTO ticketDTO = bookingService.scanQRBookingCode(bookingNumber);
        return ResponseEntity.ok(ticketDTO);
    }
    @GetMapping("/download/{bookingId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long bookingId) throws Exception {
        BookingTicketDTO ticket = bookingService.generateBookingReceipt(bookingId);
        byte[] pdfBytes = bookingService.generateReceiptInPdf(bookingId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket_receipt.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
        @GetMapping("all-booking/{scheduleId}")
    public ResponseEntity<?> findAllBooking(  @PathVariable Long scheduleId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "50") int size){
        Page allBooking= bookingService.findAllBookingsBySchedule(size, page, scheduleId);
        return ResponseEntity.ok(allBooking);
    }
    @GetMapping("/get-all-bookings")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?>getAllBookings(@RequestParam(required = false)
                                               BookingStatus bookingStatus,
                                           @RequestParam(required = false) AgeRange ageRange,
                                           @RequestParam(required = false)TrainClass trainClass,
                                           @RequestParam(required = false)PaymentMethod paymentMethod,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate travelDate,
                                           int page, int size){
        Page<Booking> allBooking = bookingService.getAllBookings(bookingStatus, ageRange, trainClass, paymentMethod, travelDate,
                page, size);
        return ResponseEntity.ok(allBooking);


    }
    @GetMapping("/get-all-bookingStatus")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getBookingStatus(){
       List<BookingStatus> bookingStatusList =  bookingService.findAllBookingStatus();
        return ResponseEntity.ok(bookingStatusList);
    }


    @GetMapping("/bookings-history")
    public ResponseEntity<?> myBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("You must login first!");
        }

        String authenticatedEmail = user.getEmail();

        Page<Booking> pastBookings = bookingService.bookingHistory(authenticatedEmail, page, size);
        return ResponseEntity.ok(pastBookings);

    }





}



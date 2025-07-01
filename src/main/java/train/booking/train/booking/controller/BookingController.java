package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.service.BookingService;

import java.io.IOException;
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
    public ResponseEntity<?> getBookingIdByPnr(@PathVariable String pnrCode) {
        Optional<Booking> booking = bookingService.findBookingByBookingNumber(pnrCode);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/receipt/{bookingId}")

    public  ResponseEntity<?> getBookingReceipt(@PathVariable  Long bookingId) throws IOException {
        BookingTicketDTO booking =bookingService.generateBookingReceipt(bookingId);
        return ResponseEntity.ok(booking);

    }

    @GetMapping("/{bookingId}/receipt/download")
    public ResponseEntity<byte[]> downloadBookingReceipt(@PathVariable Long bookingId) {
        try {
            byte[] pdfBytes = bookingService.downloadBookingReceipt(bookingId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "booking-receipt-" + bookingId + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("scan/qr/{bookingNumber}")
    public ResponseEntity<?> getBookingByQr(@PathVariable String bookingNumber) {
        BookingTicketDTO ticketDTO = bookingService.scanQRBookingCode(bookingNumber);
        return ResponseEntity.ok(ticketDTO);
    }





}




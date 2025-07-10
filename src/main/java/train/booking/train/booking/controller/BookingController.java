package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
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

    public  ResponseEntity<?> getBookingReceipt(@PathVariable  Long bookingId) throws IOException, UnirestException {
        BookingTicketDTO booking =bookingService.generateBookingReceipt(bookingId);
        return ResponseEntity.ok(booking);

    }


    @GetMapping("scan/qr/{bookingNumber}")
    public ResponseEntity<?> getBookingByQr(@PathVariable String bookingNumber) {
        BookingTicketDTO ticketDTO = bookingService.scanQRBookingCode(bookingNumber);
        return ResponseEntity.ok(ticketDTO);
    }
    @GetMapping("/download/{bookingId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long bookingId) throws Exception {
        BookingTicketDTO ticket = bookingService.generateBookingReceipt(bookingId); // call your existing method
        byte[] pdfBytes = bookingService.generateReceiptPdf(bookingId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket_receipt.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }





}




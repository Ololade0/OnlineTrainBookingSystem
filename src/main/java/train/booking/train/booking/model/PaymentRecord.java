package train.booking.train.booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;

import java.time.LocalDateTime;


    @Entity
    public class PaymentRecord {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Long bookingId;
        private Long transactionId;
        private PaymentMethod paymentChannel;
        private PaymentStatus paymentStatus;
        private LocalDateTime timestamp = LocalDateTime.now();
        private BookingStatus bookingStatus;



}

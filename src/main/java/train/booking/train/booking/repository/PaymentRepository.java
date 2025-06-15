package train.booking.train.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.BookingPayment;

@Repository
public interface PaymentRepository extends JpaRepository<BookingPayment, Long> {


    BookingPayment findByTransactionReference(String paymentId);
}

package train.booking.train.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.BookingPayment;

@Repository
public interface PaymentRepository extends JpaRepository<BookingPayment, Long> {


//    BookingPayment findByTransactionReference(String paymentId);

    @Query("SELECT bp FROM payments bp WHERE bp.transactionReference = :transactionReference")
    BookingPayment findByTransactionReference(@Param("transactionReference") String transactionReference);

//    BookingPayment findByTransactionReference(@Param("transactionReference") String transactionReference);

}

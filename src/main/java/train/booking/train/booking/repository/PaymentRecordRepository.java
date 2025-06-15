package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import train.booking.train.booking.model.PaymentRecord;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
}

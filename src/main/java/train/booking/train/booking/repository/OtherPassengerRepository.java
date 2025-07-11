package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.OtherPassenger;

import java.util.List;

@Repository
public interface OtherPassengerRepository extends JpaRepository<OtherPassenger, Long> {


    List<OtherPassenger> findByBooking_BookingId(Long bookingId);
}

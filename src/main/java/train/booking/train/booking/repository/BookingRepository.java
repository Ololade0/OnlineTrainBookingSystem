package train.booking.train.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookingNumber(String bookingNumber);
    Booking findByBookingNumber(String bookingNumber);

    Booking findByUser_Email(String email);

    @Query("SELECT b FROM bookings b WHERE b.scheduleId = :scheduleId")
    Page<Booking> findAllByScheduleId(@Param("scheduleId") Long scheduleId, Pageable pageable);

    Page<Booking> findByUserAndTravelTimeBefore(User foundUser, LocalTime now, Pageable pageable);
}

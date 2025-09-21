package train.booking.train.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.TrainClass;

import java.time.LocalDate;
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

    @Query("SELECT b FROM bookings b " +
            "WHERE b.user.id = :userId " +
            "AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus) " +
            "AND (:ageRange IS NULL OR b.ageRange = :ageRange) " +
            "AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod) " +
            "AND (:travelDate IS NULL OR b.travelDate = :travelDate)")
    Page<Booking> findBookingHistoryForASingleUser(
            @Param("userId") Long foundUser,
            @Param("bookingStatus") BookingStatus bookingStatus,
            @Param("ageRange") AgeRange ageRange,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("travelDate") LocalDate travelDate,
            Pageable pageable);

    @Query("SELECT b FROM bookings b" +
            " WHERE (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)" +
            "AND (:ageRange IS NULL OR b.ageRange = :ageRange) " +
            "AND (:trainClass IS NULL OR b.trainClass = :trainClass) " +
            "AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod) " +
            "AND (:travelDate IS NULL OR b.travelDate = :travelDate)")
    Page<Booking> findAllBookings(@Param("bookingStatus")BookingStatus bookingStatus,
                                  @Param("ageRange") AgeRange ageRange,
                                  @Param("trainClass") TrainClass trainClass,

                                  @Param("paymentMethod") PaymentMethod paymentMethod,
                                  @Param("travelDate")LocalDate localDate,
                                  Pageable pageable
                                  );


}

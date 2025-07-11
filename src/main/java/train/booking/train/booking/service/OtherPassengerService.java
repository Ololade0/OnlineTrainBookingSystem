package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;

import java.util.List;

public interface OtherPassengerService {
    List<OtherPassenger> addNewPassenger(BookingRequestDTO bookingRequestDTO, Long userId, Booking booking);

    List<OtherPassenger> findByBookingId(Long bookingId);

    void bookSeatForOtherPassengers(PaymentSuccessDTO dto, Booking booking);
}

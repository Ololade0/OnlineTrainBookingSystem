package train.booking.train.booking.service;

import train.booking.train.booking.dto.BookingDTO;
import train.booking.train.booking.model.Booking;

public interface BookingService {


   Booking createBooking(BookingDTO bookingDTO);

}

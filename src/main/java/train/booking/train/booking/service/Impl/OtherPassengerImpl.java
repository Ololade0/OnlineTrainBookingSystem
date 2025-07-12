package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.repository.OtherPassengerRepository;
import train.booking.train.booking.service.NotificationService;
import train.booking.train.booking.service.OtherPassengerService;
import train.booking.train.booking.service.SeatService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtherPassengerImpl implements OtherPassengerService {
    private final OtherPassengerRepository otherPassengerRepository;

    private final NotificationService notificationService;

    private final SeatService seatService;

    @Override
    public List<OtherPassenger> addNewPassenger(BookingRequestDTO bookingRequestDTO, Long foundUser, Booking primaryBooking) {
        List<OtherPassenger> savedAdditinalPassenger = new ArrayList<>();
        if (bookingRequestDTO.getAdditionalPassenger() != null) {
            for (OtherPassenger additionalPassenger : bookingRequestDTO.getAdditionalPassenger()) {
                OtherPassenger savedPassenger = OtherPassenger.builder()
                        .name(additionalPassenger.getName())
                        .email(additionalPassenger.getEmail())
                        .gender(additionalPassenger.getGender())
                        .phoneNumber(additionalPassenger.getPhoneNumber())
                        .idNumber(additionalPassenger.getIdNumber())
                        .identificationType(additionalPassenger.getIdentificationType())
                        .passengerType(additionalPassenger.getPassengerType())
                        .seatNumber(additionalPassenger.getSeatNumber())
                        .booking(primaryBooking)
                        .userId(foundUser)
                        .build();
                otherPassengerRepository.save(savedPassenger);
                savedAdditinalPassenger.add(savedPassenger);
            }
        }
        return savedAdditinalPassenger;
    }

    @Override
    public List<OtherPassenger> findByBookingId(Long bookingId) {
        return otherPassengerRepository.findByBooking_BookingId(bookingId);
    }
@Override
    public void bookSeatForOtherPassengers(PaymentSuccessDTO dto, Booking booking) {
        List<OtherPassenger> otherPassengers = otherPassengerRepository.findByBooking_BookingId(dto.getBookingId());
        for (OtherPassenger passenger : otherPassengers) {
            if (passenger.getSeatNumber() != null && passenger.getSeatNumber() > 0) {
                try {
                    BookSeatDTO otherSeat = new BookSeatDTO();
                    otherSeat.setTrainClass(booking.getTrainClass());
                    otherSeat.setSeatNumber(passenger.getSeatNumber());
                    otherSeat.setBookingId(booking.getBookingId());
                    seatService.bookSeat(otherSeat);
                    otherSeat.setAvailable(false);
                    notificationService.webSocketNotification(otherSeat);
                } catch (Exception e) {
                    log.error("Failed to book seat for other passenger {}: {}", passenger.getName(), e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                log.warn("Other passenger seatNumber missing or invalid: {}", passenger.getName());
            }
        }
    }

}

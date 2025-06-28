package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.repository.OtherPassengerRepository;
import train.booking.train.booking.service.OtherPassengerService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtherPassengerImpl implements OtherPassengerService {
    private final OtherPassengerRepository otherPassengerRepository;

//    private final SeatService seatService;

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

}

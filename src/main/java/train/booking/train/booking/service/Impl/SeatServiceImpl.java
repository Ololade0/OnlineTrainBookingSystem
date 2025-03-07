package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.exceptions.InvalidSeatNumberException;
import train.booking.train.booking.exceptions.SeatAlreadyBookedException;
import train.booking.train.booking.exceptions.SeatCannotBeFoundException;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.TrainClass;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.repository.SeatRepository;
import train.booking.train.booking.service.SeatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    /**
     * Generates seats for a given train class.
     * @param startSeat the starting seat number
     * @param endSeat the ending seat number
     * @param trainClass the train class
     * @return the list of generated seats
     */
    @Transactional
    public List<Seat> generateSeats(int startSeat, int endSeat, TrainClass trainClass) {
        if (startSeat > endSeat) {
            throw new IllegalArgumentException("Start seat number cannot be greater than end seat number.");
        }

        log.info("Generating seats from {} to {} for train class: {}", startSeat, endSeat, trainClass.getTrainClassName());

        List<Seat> seats = new ArrayList<>();
        for (int i = startSeat; i <= endSeat; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(i);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setTrainClass(trainClass);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
        log.info("{} seats successfully generated for class: {}", seats.size(), trainClass.getTrainClassName());
        return seats;
    }

    /**
     * Books a seat for a given train class and seat number.
     * @param trainClassName the name of the train class
     * @param seatNumber the seat number
     * @return the booked seat
     */
    @Override
    public Seat bookSeat(String trainClassName, int seatNumber) {
        log.info("Attempting to book seat {} in class {}", seatNumber, trainClassName);

        Seat seatToBook = seatRepository.findByTrainClass_TrainClassNameAndSeatNumber(trainClassName, seatNumber)
                .orElseThrow(() -> new InvalidSeatNumberException("Seat number " + seatNumber + " does not exist in class " + trainClassName));

        if (seatToBook.getStatus() == SeatStatus.BOOKED) {
            log.warn("Seat {} in class {} is already booked.", seatNumber, trainClassName);
            throw new SeatAlreadyBookedException("Seat number " + seatNumber + " has already been booked.");
        }

        seatToBook.setStatus(SeatStatus.BOOKED);
        Seat bookedSeat = seatRepository.save(seatToBook);
        log.info("Seat {} in class {} successfully booked.", seatNumber, trainClassName);
        return bookedSeat;
    }

    /**
     * Finds a seat by its seat number.
     * @param seatNumber the seat number
     * @return the found seat
     */
    @Override
    public Seat findSeat(int seatNumber) {
        log.info("Searching for seat with number {}", seatNumber);
        return seatRepository.findBySeatNumber(seatNumber)
                .orElseThrow(() -> new SeatCannotBeFoundException("Seat with number " + seatNumber + " cannot be found."));
    }

    /**
     * Finds a seat by its ID.
     * @param seatId the seat ID
     * @return the found seat
     */
    @Override
    public Seat findSeatById(Long seatId) {
        log.info("Searching for seat with ID {}", seatId);
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatCannotBeFoundException("Seat cannot be found with ID: " + seatId));
    }

    /**
     * Updates a seat.
     * @param seat the seat to update
     */
    public void updateSeat(Seat seat) {
        log.info("Updating seat with ID {}", seat.getId());
        seatRepository.save(seat);
        log.info("Seat {} updated successfully.", seat.getId());
    }

    /**
     * Retrieves all seats.
     * @return the list of all seats
     */
    @Override
    public List<Seat> findAllSeat() {
        log.info("Retrieving all seats");
        return seatRepository.findAll();
    }



    /**
     * Finds a seat by class name and seat number.
     * @param className the class name
     * @param seatNumber the seat number
     * @return the found seat
     */
    public Seat findSeatByClassAndNumber(String className, int seatNumber) {
        log.info("Searching for seat {} in class {}", seatNumber, className);
        return seatRepository.findByTrainClass_TrainClassNameAndSeatNumber(className, seatNumber)
                .orElseThrow(() -> new SeatCannotBeFoundException("Seat not found for class: " + className + " and seat number: " + seatNumber));
    }
}

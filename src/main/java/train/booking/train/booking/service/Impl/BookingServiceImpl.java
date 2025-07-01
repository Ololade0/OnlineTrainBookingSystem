package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.*;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.BookingCannotBeFoundException;
import train.booking.train.booking.model.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.*;
import train.booking.train.booking.utils.PnrCodeGenerator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
        private final BookingRepository bookingRepository;
        private final UserService userService;
        private final ScheduleService scheduleService;
        private final SeatService seatService;
        private final PnrCodeGenerator pnrCodeGenerator;
        private final JmsTemplate jmsTemplate;
        private final ObjectMapper objectMapper;
        private final TrainService trainService;
        private final StationService stationService;

        @Value("${qrbase.Url}")
        private String qrbaseUrl;

@Transactional
@Override
    public BookingResponse createBooking(BookingRequestDTO bookingDTO) {
        User user = userService.findUserById(bookingDTO.getUserId());
        ScheduleResponse scheduleResponse = scheduleService.findSchedule(
                bookingDTO.getDepartureStationId(),
                bookingDTO.getArrivalStationId(),
                bookingDTO.getDepartureDate()
        );
        seatService.checkSeatAvailability(bookingDTO.getSeatNumber(), scheduleResponse.getScheduleId(), bookingDTO.getTrainClass());
   BigDecimal totalFare = calculateTotalFare(bookingDTO, scheduleResponse);
    String pnrCode = pnrCodeGenerator.generateUniquePnrCodes();
        BookingQueueDTO bookingQueueDTO = buildBookingQueueDTO(bookingDTO, user.getId(), scheduleResponse, pnrCode, totalFare);
        try {
            String jsonPayload = objectMapper.writeValueAsString(bookingQueueDTO);
            jmsTemplate.convertAndSend("bookingQueue", jsonPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BookingQueueDTO", e);
        }

        return BookingResponse.builder()
                .pnrCode(pnrCode)
                .totalFare(totalFare)
                .message("Your seat has been reserved for 10 minutes.")
                .build();

    }

    private BigDecimal calculateTotalFare(BookingRequestDTO bookingDTO, ScheduleResponse scheduleResponse) {
        BigDecimal convenienceCharge = BigDecimal.valueOf(200);
        BigDecimal totalFare = BigDecimal.ZERO;

        BigDecimal mainFare = extractFare(scheduleResponse, bookingDTO.getTrainClass(), bookingDTO.getPassengerType());
        totalFare = totalFare.add(mainFare).add(convenienceCharge);

        if (bookingDTO.getAdditionalPassenger() != null) {
            for (OtherPassenger additional : bookingDTO.getAdditionalPassenger()) {
                BigDecimal additionalFare = extractFare(scheduleResponse, bookingDTO.getTrainClass(), additional.getPassengerType());
                totalFare = totalFare.add(additionalFare).add(convenienceCharge);
            }
        }

        return totalFare;
    }


    private BigDecimal extractFare(ScheduleResponse scheduleResponse, TrainClass trainClass, AgeRange ageRange) {
        return scheduleResponse.getSchedules().stream()
                .filter(schedule -> schedule.getScheduleId().equals(scheduleResponse.getScheduleId()))
                .flatMap(schedule -> schedule.getPrices().stream())
                .filter(price -> price.getTrainClass().equals(trainClass)
                        && price.getAgeRange().equals(ageRange))
                .map(PriceListDTO::getPrice)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No price found for selected train class and age range"));
    }

    private BookingQueueDTO buildBookingQueueDTO(
            BookingRequestDTO bookingDTO,
            Long userId,
            ScheduleResponse scheduleResponse,
            String bookingNumber,
            BigDecimal totalFare
    ) {
        return BookingQueueDTO.builder()
                .userId(userId)
                .scheduleId(scheduleResponse.getScheduleId())
                .travelDate(scheduleResponse.getDepartureDate())
                .travelTime(scheduleResponse.getDepartureTime())
                .trainClass(bookingDTO.getTrainClass())
                .seatNumber(bookingDTO.getSeatNumber())
                .bookingNumber(bookingNumber)
                .passengerType(bookingDTO.getPassengerType())
                .bookingRequestDTO(bookingDTO)
                .totalFare(totalFare)
                .build();
    }
    @Transactional(rollbackFor = Exception.class)
    public Booking saveBooking(BookingQueueDTO dto) {
        User user = userService.findUserById(dto.getUserId());
        Schedule schedule = scheduleService.findSchedulesById(dto.getScheduleId());
        Booking booking = Booking.builder()
                .bookingDate(LocalDateTime.now())
                .user(user)
                .scheduleId(schedule.getId())
                .passengerType(dto.getPassengerType())
                .travelDate(schedule.getDepartureDate())
                .travelTime(schedule.getDepartureTime())
                .trainClass(dto.getTrainClass())
                .seatNumber(dto.getSeatNumber())
                .bookingStatus(BookingStatus.RESERVED)
                .totalFareAmount(dto.getTotalFare())
                .bookingNumber(dto.getBookingNumber())
                .build();

        return bookingRepository.save(booking);
    }



    @Override
        public Booking findBookingById(Long bookingId) {
          return bookingRepository.findById(bookingId).orElseThrow(()->
               new BookingCannotBeFoundException("Booking with Id" + bookingId + " cannot be found ")

          );
        }
    @Override
        public Optional<Booking> findBookingByBookingNumber(String bookingNumber){
   Booking foundBooking = bookingRepository.findByBookingNumber(bookingNumber);
   if(foundBooking == null){
       throw new BookingCannotBeFoundException("Booking with pnr " + bookingNumber + " cannot be found");
   }
   return Optional.of(foundBooking);
        }





    @Override
    public Booking updateBookingStatus(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        booking.setBookingStatus(BookingStatus.BOOKED);
        return bookingRepository.save(booking);
    }



    @Override
    public BookingTicketDTO generateBookingReceipt(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        Schedule schedule = scheduleService.findSchedulesById(booking.getScheduleId());
        Train train = trainService.findTrainById(schedule.getTrainId());
        Station departureStation = stationService.findStationById(schedule.getDepartureStationId());
        Station arrivalStation = stationService.findStationById(schedule.getArrivalStationId());
        BookingPayment foundPayment = booking.getBookingPayment();

        return mapBookingTicket(booking, schedule, train, departureStation, arrivalStation, foundPayment);
    }

    private static BookingTicketDTO mapBookingTicket(Booking booking, Schedule schedule, Train train, Station departureStation, Station arrivalStation, BookingPayment foundPayment) {
        BookingTicketDTO ticket = new BookingTicketDTO();
        ticket.setTrainName(train.getTrainName());
        ticket.setTrainCode(train.getTrainCode());
        ticket.setTravelDate(booking.getTravelDate());
        ticket.setBookingNumber(booking.getBookingNumber());
        ticket.setBookingStatus(booking.getBookingStatus());
        ticket.setSourceStation(departureStation.getStationName());
        ticket.setDestinationStation(arrivalStation.getStationName());
        ticket.setDepartureTime(schedule.getDepartureTime());
        ticket.setArrivalTime(schedule.getArrivalTime());
        ticket.setTrainClass(booking.getTrainClass());
        ticket.setPaymentMethod(foundPayment.getPaymentMethod());
        ticket.setTotalFare(booking.getTotalFareAmount());
        ticket.setFirstName(booking.getUser().getFirstName());
        ticket.setAgeRange(booking.getPassengerType());
        ticket.setSeatNumber(booking.getSeatNumber());
        ticket.setIdentificationType(booking.getUser().getIdentificationType());
        ticket.setIdNumber(booking.getUser().getIdNumber());

        // Convert OtherPassenger to DTOs
        List<OtherPassenger> otherPassengerDTOList = new ArrayList<>();
        if (booking.getOtherPassengers() != null) {
            for (OtherPassenger passenger : booking.getOtherPassengers()) {
                OtherPassenger dto = new OtherPassenger();
                dto.setName(passenger.getName());
                dto.setEmail(passenger.getEmail());
                dto.setSeatNumber(passenger.getSeatNumber());
                dto.setIdNumber(passenger.getIdNumber());
                dto.setIdentificationType(passenger.getIdentificationType());
                dto.setPassengerType(passenger.getPassengerType());
                otherPassengerDTOList.add(dto);
            }
        }
        ticket.setOtherPassengers(otherPassengerDTOList);
        return ticket;
    }



    public byte[] downloadBookingReceipt(Long bookingId) throws Exception {
        BookingTicketDTO ticket = generateBookingReceipt(bookingId);

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Train Ticket Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Booking Info
        document.add(new Paragraph("Booking Number: " + ticket.getBookingNumber()));
        document.add(new Paragraph("Status: " + ticket.getBookingStatus()));
        document.add(new Paragraph("Passenger: " + ticket.getFirstName()));
        document.add(new Paragraph("Travel Date: " + ticket.getTravelDate()));
        document.add(new Paragraph("From: " + ticket.getSourceStation()));
        document.add(new Paragraph("To: " + ticket.getDestinationStation()));
        document.add(new Paragraph("Departure Time: " + ticket.getDepartureTime()));
        document.add(new Paragraph("Arrival Time: " + ticket.getArrivalTime()));
        document.add(new Paragraph("Train: " + ticket.getTrainName() + " (" + ticket.getMapBookingTicketDTO() + ")"));
        document.add(new Paragraph("Seat Number: " + ticket.getSeatNumber()));
        document.add(new Paragraph("Class: " + ticket.getTrainClass()));
        document.add(new Paragraph("Passenger Type: " + ticket.getAgeRange()));
        document.add(new Paragraph("Fare: " + ticket.getTotalFare()));
        document.add(new Paragraph("Payment Method: " + ticket.getPaymentMethod()));
        document.add(new Paragraph("ID Type: " + ticket.getIdentificationType()));
        document.add(new Paragraph("ID Number: " + ticket.getIdNumber()));
        document.add(new Paragraph(" "));

        // Other Passengers (if any)
        List<OtherPassenger> others = ticket.getOtherPassengers();
        if (others != null && !others.isEmpty()) {
            document.add(new Paragraph("Other Passengers:"));
            for (OtherPassenger p : others) {
                document.add(new Paragraph(" - " + p.getName() + ", " + p.getPassengerType() + ", Seat: " + p.getSeatNumber()));
            }
        }

        document.add(new Paragraph(" ")); // spacing

        String qrContent = qrbaseUrl + ticket.getBookingNumber();
        Image qrImage = generateQRCodeImage(qrContent);
        qrImage.scaleToFit(150, 150);
        document.add(new Paragraph("QR Code:"));
        document.add(qrImage);

        document.close();
        return baos.toByteArray();
    }



    public Image generateQRCodeImage(String text) throws Exception {
        int width = 200;
        int height = 200;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Image.getInstance(pngData);
    }



    @Override
    public BookingTicketDTO scanQRBookingCode(String bookingNumber) {
      Optional<Booking> foundBooking= findBookingByBookingNumber(bookingNumber);
        return mapToBookingTicketDTO(foundBooking.get());
    }

    private BookingTicketDTO mapToBookingTicketDTO(Booking booking) {
        BookingTicketDTO dto = new BookingTicketDTO();
        dto.setBookingNumber(booking.getBookingNumber());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setFirstName(booking.getUser().getFirstName());
        dto.setTravelDate(booking.getTravelDate());
         dto.setSeatNumber(booking.getSeatNumber());
        dto.setTrainClass(booking.getTrainClass());
        dto.setTotalFare(booking.getTotalFareAmount());
        dto.setIdNumber(booking.getUser().getIdNumber());
        // Other Passengers
        List<OtherPassenger> passengers = booking.getOtherPassengers().stream()
                .map(p -> new OtherPassenger(p.getName(), p.getPassengerType(), p.getSeatNumber()))
                .collect(Collectors.toList());
        dto.setOtherPassengers(passengers);
        return dto;
    }


}






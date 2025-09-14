package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import train.booking.train.booking.dto.*;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.BookingCannotBeFoundException;
import train.booking.train.booking.exceptions.PriceCannotBeFoundException;
import train.booking.train.booking.model.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.*;
import train.booking.train.booking.utils.PnrCodeGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
  private final TemplateEngine templateEngine;


        @Value("${qrbase.url}")
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
                .orElseThrow(() -> new PriceCannotBeFoundException("No price found for selected train class and age range"));
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
                .ageRange(dto.getPassengerType())
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
    public BookingTicketDTO generateBookingReceipt(Long bookingId) throws UnirestException {
        Booking booking = findBookingById(bookingId);
        Schedule schedule = scheduleService.findSchedulesById(booking.getScheduleId());
        Train train = trainService.findTrainById(schedule.getTrainId());
        Station departureStation = stationService.findStationById(schedule.getDepartureStationId());
        Station arrivalStation = stationService.findStationById(schedule.getArrivalStationId());
        BookingPayment foundPayment = booking.getBookingPayment();
        //          notificationService.sendBookingReceipts(booking.getUser().getEmail(), "BOOKING RECEIPT", bookingTicketDTO);
          return mapBookingTicket(booking, schedule, train, departureStation, arrivalStation, foundPayment);
    }

    private static BookingTicketDTO mapBookingTicket(Booking booking, Schedule schedule, Train train, Station departureStation, Station arrivalStation, BookingPayment foundPayment) {
        BookingTicketDTO ticket = new BookingTicketDTO();
        ticket.setMessage("Thank you for using our online booking service.");
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
        ticket.setAgeRange(booking.getAgeRange());
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
    @Override
    public byte[] generateReceiptInPdf(Long bookingId) throws Exception {
        BookingTicketDTO ticket = generateBookingReceipt(bookingId);
        Context context = new Context();
        context.setVariable("ticket", ticket);
        String qrBase64 = generateQRCodeBase64(qrbaseUrl + ticket.getBookingNumber());
        context.setVariable("qrCodeBase64", qrBase64);
//        context.setVariable("qrCodeBase64", "data:image/png;base64," + generateQRCodeBase64(qrbaseUrl + ticket.getBookingNumber()));
//        context.setVariable("qrCodeBase64", generateQRCodeBase64(qrbaseUrl + ticket.getBookingNumber()));
        String html = templateEngine.process("pdf-receipt", context);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        }
    }
    @Override
    public String generateQRCodeBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(pngData);
    }


    @Override
    public BookingTicketDTO scanQRBookingCode(String bookingNumber) {
      Optional<Booking> foundBooking= findBookingByBookingNumber(bookingNumber);
        return mapToBookingTicketDTO(foundBooking.get());
    }

    @Override
    public Page<Booking> findAllBookingsBySchedule(int size, int page, Long scheduleId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        return bookingRepository.findAllByScheduleId(scheduleId, pageable);
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

    @Override
    public Page<Booking> bookingHistory(String email, int page, int size) {
        User foundUser =userService.findUserByEmailOrNull(email);
        LocalTime now = LocalTime.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,
                "travelTime"));
       Page<Booking> pastBooking =  bookingRepository.findByUserAndTravelTimeBefore(foundUser, now, pageable);
       return pastBooking;
    }

    @Override
    public Page<Booking> getAllBookings(BookingStatus bookingStatus,AgeRange ageRange, TrainClass trainClass,
                                        PaymentMethod paymentMethod, LocalDate localDate, int page, int size) {
    Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "bookingDate"));
      return bookingRepository.findAllBookings(bookingStatus,ageRange, trainClass, paymentMethod, localDate, pageable);
    }

    @Override
    public List<BookingStatus> findAllBookingStatus() {
        return Arrays.asList(BookingStatus.values());
    }


}






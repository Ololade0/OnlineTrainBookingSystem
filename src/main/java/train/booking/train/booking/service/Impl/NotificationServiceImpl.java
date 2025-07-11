package train.booking.train.booking.service.Impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.dto.MailDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.service.NotificationService;

import java.util.concurrent.CompletableFuture;

;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RestTemplate restTemplate;

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${mailgun.domain}")
    private String DOMAIN;
    @Value("${mailgun.api-key}")
    private String PRIVATE_KEY;
    @Value("${mailgun.api-url}")
    private String APU_URL;

    @Value("${mail.gun.activation}")
    private String ACTIVATION_URL;



    @Override
    @Async
    public CompletableFuture<MailDTO> sendSimpleMail(MailDTO mailRequest) throws UnirestException {
            log.info("DOMAIN -> {}", DOMAIN);
            log.info("API KEY -> {}", PRIVATE_KEY);
            log.info(mailRequest.getBody());
            try {
            HttpResponse<String> request = Unirest.post(APU_URL + DOMAIN + "/messages")
                    .basicAuth("api", PRIVATE_KEY)
                    .queryString("from", mailRequest.getSender())
                    .queryString("to", mailRequest.getReceiver())
                    .queryString("subject", mailRequest.getSubject())
                    .queryString("text", mailRequest.getBody())
                    .asString();
            MailDTO mailResponse = request.getStatus() == 200 ? new MailDTO(true) : new MailDTO(false);
            return CompletableFuture.completedFuture(mailResponse);

        }

            catch (UnirestException exception){
                log.error("Error sending mail: ", exception);
                return CompletableFuture.completedFuture(new MailDTO(false));
            }

            }

    @Override
    public BaseResponse sendMail(String email, String name) throws UnirestException {
        MailDTO mailRequest = MailDTO.builder()
                .sender(System.getenv("SENDER"))
                .receiver(email)
                .subject("You are welcome")
                .body("Hello " + name + ". Your account has been sucessfully activated !!!   Thank you for travelling  with us")
                .build();
            sendSimpleMail(mailRequest);
            return ResponseUtil.success("Email sucessfully sent", null);
    }



    @Override
    public BaseResponse sendActivationEmail(String email,  String name, String subject,  String token) throws UnirestException {
        String activationLink = ACTIVATION_URL + token;

        MailDTO mailRequest = MailDTO.builder()
                .sender("no-reply@yourdomain.com")
                .receiver(email)
                .subject(subject)
                .body("Dear, " + name + " Click the link to activate your account: " + activationLink)
                .build();
        sendSimpleMail(mailRequest);
        return ResponseUtil.success("Activation Link sucessfully sent", null);

    }


    @Override
    public BaseResponse sendBookingReceipts(String email, BookingTicketDTO bookingTicketDTO) throws UnirestException {
      MailDTO mailDTO = MailDTO.builder()
              .sender("no-reply@yourdomain.com")
              .receiver(email)
              .subject("Ticket Booking Confirmation")
              .body(" Dear " + bookingTicketDTO.getFirstName()  + " Thank you for using the Nigerian Railway corportaion online ticket booking services. Your ticket booking Details are " +  bookingTicketDTO)
              .build();
      sendSimpleMail(mailDTO);
      return  ResponseUtil.success("Booking confirmation sucessfully sent", bookingTicketDTO);
    }



    @Override
    public void webSocketNotification(BookSeatDTO seatDto){
        log.info("🔔 Sending WebSocket seat update: {}", seatDto);
        messagingTemplate.convertAndSend("/topic/seats", seatDto);
    }


}


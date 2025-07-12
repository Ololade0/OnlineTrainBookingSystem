package train.booking.train.booking.service.Impl;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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

    private final WebClient.Builder webClientBuilder;



    private final Gson gson = new Gson();

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${mailgun.domain}")
    private String DOMAIN;
    @Value("${mailgun.api-key}")
    private String PRIVATE_KEY;
    @Value("${mailgun.api-url}")
    private String APU_URL;

    @Value("${mail.gun.activation}")
    private String ACTIVATION_URL;

    @Value("${email.api.url}")
    private String emailApiUrl;

    @Value("${email.sender}")
    private String sender;

    @Autowired
    private Environment environment;


//    @PostConstruct
//    public void init() {
//        Client client = ClientBuilder.newClient();
//        this.emailRestClient = client.target(emailApiUrl);
//    }



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
                    .queryString("to", mailRequest.getRecipient())
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

//    @Override
//    public BaseResponse sendMail(String email, String name) throws UnirestException {
//        MailDTO mailRequest = MailDTO.builder()
//                .sender(System.getenv("SENDER"))
//                .recipient(email)
//                .subject("You are welcome")
//                .body("Hello " + name + ". Your account has been sucessfully activated !!!   Thank you for travelling  with us")
//                .build();
//            sendSimpleMail(mailRequest);
//            return ResponseUtil.success("Email sucessfully sent", null);
//    }
//
//
//
//    @Override
//    public BaseResponse sendEmail2(String email, String name, String subject, String token) throws UnirestException {
//        String activationLink = ACTIVATION_URL + token;
//
//        MailDTO mailRequest = MailDTO.builder()
//                .sender("no-reply@yourdomain.com")
//                .recipient(email)
//                .subject(subject)
//                .body(token)
//                .build();
//        sendSimpleMail(mailRequest);
//        return ResponseUtil.success("Activation Link sucessfully sent", null);
//
//    }


    @Override
    public BaseResponse sendBookingReceipts(String email, BookingTicketDTO bookingTicketDTO) throws UnirestException {
      MailDTO mailDTO = MailDTO.builder()
              .sender("no-reply@yourdomain.com")
              .recipient(email)
              .subject("Ticket Booking Confirmation")
              .body(" Dear " + bookingTicketDTO.getFirstName()  + " Thank you for using the Nigerian Railway corportaion online ticket booking services. Your ticket booking Details are " +  bookingTicketDTO)
              .build();
      sendSimpleMail(mailDTO);
      return  ResponseUtil.success("Booking confirmation sucessfully sent", bookingTicketDTO);
    }



    @Override
public void sendEmailV3(String recipient, String subject, String body) {
        String activationLink = ACTIVATION_URL + body;
    MailDTO request = new MailDTO();
    request.setSender(sender);
    request.setRecipient(recipient);
    request.setSubject(subject);
    request.setBody(body);

    webClientBuilder.build()
            .post()
            .uri(emailApiUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                    clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                        log.error("Failed to send email: {}", errorBody);
                        return Mono.error(new RuntimeException("Email API Error: " + errorBody));
                    })
            )
            .bodyToMono(String.class)
            .doOnSuccess(response -> log.info("Email API response: {}", response))
            .doOnError(error -> log.error("Error sending email", error))
            .subscribe();
}


    @Override
    public void webSocketNotification(BookSeatDTO seatDto){
        log.info("🔔 Sending WebSocket seat update: {}", seatDto);
        messagingTemplate.convertAndSend("/topic/seats", seatDto);
    }


}






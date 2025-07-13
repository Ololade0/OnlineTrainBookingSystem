package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.MailDTO;
import train.booking.train.booking.service.NotificationService;

;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final WebClient.Builder webClientBuilder;


    private final SimpMessagingTemplate messagingTemplate;

    @Value("${activation.url}")
    private String ACTIVATION_URL;

    @Value("${email.url}")
    private String emailApiUrl;

    @Value("${email.sender}")
    private String sender;


    @Override
    public void sendBookingReceipts(String recipient, String subject, String body) {
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
            public void sendEmailV3(String recipient, String subject, String body) {
                String activationLink = ACTIVATION_URL + body;
            MailDTO request = new MailDTO();
            request.setSender(sender);
            request.setRecipient(recipient);
            request.setSubject(subject);
            request.setBody(activationLink);

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






package train.booking.train.booking.service;

;
import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.request.MailRequest;
import train.booking.train.booking.dto.response.MailResponse;

import com.mashape.unirest.http.HttpResponse;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailServicesImpl implements EmailService{
//    private final String DOMAIN = System.getenv("DOMAIN");
//    private final String PRIVATE_KEY = System.getenv("API_KEY");

    @Value("${mailgun.domain}")
    private String DOMAIN;
    @Value("${mailgun.api-key}")
    private String PRIVATE_KEY;
    @Value("${mailgun.api-url}")
    private String APU_URL;
    @Override
    @Async
    public CompletableFuture<MailResponse> sendSimpleMail(MailRequest mailRequest) throws UnirestException {
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
            MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);
            return CompletableFuture.completedFuture(mailResponse);

        }

            catch (UnirestException exception){
                log.error("Error sending mail: ", exception);
                return CompletableFuture.completedFuture(new MailResponse(false));
            }

            }
    }


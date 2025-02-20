package train.booking.train.booking.service;


import com.mashape.unirest.http.exceptions.UnirestException;
import train.booking.train.booking.dto.request.MailRequest;
import train.booking.train.booking.dto.response.MailResponse;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<MailResponse> sendSimpleMail(MailRequest mailRequest) throws UnirestException;
}

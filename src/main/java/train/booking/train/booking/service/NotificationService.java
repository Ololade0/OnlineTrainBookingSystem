package train.booking.train.booking.service;


import com.mashape.unirest.http.exceptions.UnirestException;
import train.booking.train.booking.dto.request.MailRequest;
import train.booking.train.booking.dto.response.MailResponse;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    CompletableFuture<MailResponse> sendSimpleMail(MailRequest mailRequest) throws UnirestException;
    String sendMail(String email, String name) throws UnirestException;

  String sendActivationEmail(String email,String name,  String token) throws UnirestException;
}

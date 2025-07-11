package train.booking.train.booking.service;


import com.mashape.unirest.http.exceptions.UnirestException;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.dto.MailDTO;
import train.booking.train.booking.dto.response.BaseResponse;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    CompletableFuture<MailDTO> sendSimpleMail(MailDTO mailRequest) throws UnirestException;
    BaseResponse sendMail(String email, String name) throws UnirestException;

//  BaseResponse sendActivationEmail(String email,String name,  String token) throws UnirestException;

    BaseResponse sendBookingReceipts(String email, BookingTicketDTO bookingTicketDTO) throws UnirestException;

    BaseResponse sendActivationEmail(String email,  String name, String subject,  String token) throws UnirestException;

    void webSocketNotification(BookSeatDTO seatDto);

}

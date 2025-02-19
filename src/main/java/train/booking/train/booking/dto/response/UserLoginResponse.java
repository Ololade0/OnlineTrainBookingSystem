package train.booking.train.booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.User;

@Setter
@Getter
@Builder
public class UserLoginResponse {
    private int code;
    private String message;
    private User user;

}

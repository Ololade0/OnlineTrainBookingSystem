package train.booking.train.booking.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String token;
    private String name;
    private String email;
    private String message;
//    private User user;
    private int code;

    public UserLoginResponse(String token, String name, String email) {
        this.token = token;
        this.name = name;
        this.email = email;
    }
}

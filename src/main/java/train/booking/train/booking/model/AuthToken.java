package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.AuthTokenStatus;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "AuthToken")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String name;
    private String email;
    private Instant expiryDate;
    @Enumerated(EnumType.STRING)
    private AuthTokenStatus authTokenStatus;


    public AuthToken(String token, String name,String email) {
        this.token = token;
        this.email = email;
        this.name = name;
    }
}
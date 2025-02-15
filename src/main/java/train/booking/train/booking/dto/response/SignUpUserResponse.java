package train.booking.train.booking.dto.response;

import lombok.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.GenderType;

import java.time.LocalDate;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class SignUpUserResponse {
    private Long id;

    private String firstName;
    private String lastName;

    private String email;

    private GenderType gender;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String message;

    private String idNumber;
    private Set<Role> roles;



    ;


}

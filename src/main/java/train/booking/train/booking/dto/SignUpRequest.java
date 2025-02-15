package train.booking.train.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private GenderType gender;
    private LocalDate dateOfBirth;
    private IdentificationType identificationType;
    private String phoneNumber;
    private String password;
    private String idNumber;
    private String confirmPassword;
    private String message;
    private Set<Role> roles;
    private RoleType roleType;
//    private Long userId;
}

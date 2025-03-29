package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
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

    public UserDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


}

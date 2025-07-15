package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindAllByRolesDTO {
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private GenderType gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String idNumber;
    private IdentificationType identificationType;
    private Set roleHashSet;
    private boolean isVerified = false;

}

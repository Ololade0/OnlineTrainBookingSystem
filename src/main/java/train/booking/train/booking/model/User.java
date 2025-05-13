package train.booking.train.booking.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "First name must not be blank")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Kindly enter a valid email")
    private String email;
//    @NotNull(message = "Kindly choose your gender")
    private GenderType gender;
    private String password;
    private String confirmPassword;
    @NotBlank(message = "phonenumber must not be blank")
    private String phoneNumber;
    private LocalDate dateOfBirth;
//    @Size(min = 10, max = 15, message = "ID number must be between 10 and 15 characters")
    private String idNumber;

    private IdentificationType identificationType;

    private boolean isVerified = false;
    private String activationToken;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roleHashSet = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;


    public boolean hasRole(RoleType roleType) {
        return this.roleHashSet.stream().anyMatch(role -> role.getRoleType() == roleType);
    }


}






package train.booking.train.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@Entity(name = "trains")
@AllArgsConstructor
@NoArgsConstructor
public class Train extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Train Name is required")
    private String trainName;
    @NotBlank(message = "Train code is required ")
    private String trainCode;
    @Positive(message = "Total seats must be greater than 0")
    private int totalSeat;

    @NotEmpty(message = "Train Class is required")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<TrainClass> trainClasses = new HashSet<>();

}
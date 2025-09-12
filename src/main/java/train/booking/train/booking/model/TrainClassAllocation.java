package train.booking.train.booking.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import lombok.*;
import train.booking.train.booking.model.enums.TrainClass;
@Setter
@Getter
@Builder
@ToString
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class TrainClassAllocation {
    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;

    @Positive
    private int seatCount;
}

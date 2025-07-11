package train.booking.train.booking.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePriceDTO {

    private Long priceListId;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TrainClass trainClass;

    @Enumerated(EnumType.STRING)

    private AgeRange ageRange;

    private Long scheduleId;
    private Long stationId;
}

package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.model.enums.AgeRange;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

    public class PriceListDTO {
        private Long id;
        private TrainClass trainClass;
        private AgeRange ageRange;
        private BigDecimal price;
        private Long scheduleId;
        private Long stationId;



    public PriceListDTO(TrainClass trainClass, AgeRange ageRange, BigDecimal price) {
        this.trainClass = trainClass;
        this.ageRange = ageRange;
        this.price = price;

    }
}

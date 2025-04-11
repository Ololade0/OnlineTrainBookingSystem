package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.TrainClass;
import train.booking.train.booking.model.enums.AgeRange;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
    public class PriceListDTO {
        private Long id;
        private TrainClass trainClass;
        private AgeRange ageRange;
        private BigDecimal price;
        private Long scheduleId;
        private Long stationId;



    public PriceListDTO(Long id, BigDecimal price) {
        this.id = id;
        this.price = price;
    }
}

package train.booking.train.booking.model;


import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;
    @Entity
    @Table(name = "price_list")
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PriceList {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private BigDecimal price;

        @Enumerated(EnumType.STRING)
        private TrainClass trainClass;

        @Enumerated(EnumType.STRING)

        private AgeRange ageRange;

        private Long scheduleId;
        private Long stationId;



}

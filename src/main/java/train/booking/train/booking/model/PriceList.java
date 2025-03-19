package train.booking.train.booking.model;


import jakarta.persistence.*;
import train.booking.train.booking.model.enums.AgeRange;

import java.math.BigDecimal;

/** Entity to store price details for train bookings */
    @Entity
    @Table(name = "price_list")
    public class PriceList {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        private TrainClass trainClass;

        @Enumerated(EnumType.STRING)

        private AgeRange ageRange;

        private BigDecimal price;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "schedule_id", nullable = false)
//    private Schedule schedule;
//        private Long scheduleId;
@ManyToOne
@JoinColumn(name = "schedule_id", nullable = false)
private Schedule schedule;
}

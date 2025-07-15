package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;


@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "otherpassenger")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtherPassenger {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        private String email;

        private GenderType gender;

        private String phoneNumber;
        private String idNumber;
        private AgeRange passengerType;
        private Integer seatNumber;

        private Long userId;



        @Enumerated(EnumType.STRING)
        private IdentificationType identificationType;

        @ManyToOne
        @JoinColumn(name = "booking_id", nullable = false)
        @JsonBackReference
        private Booking booking;

        public OtherPassenger(String name, AgeRange passengerType, Integer seatNumber) {
                this.name = name;
                this.passengerType = passengerType;
                this.seatNumber = seatNumber;
        }
}



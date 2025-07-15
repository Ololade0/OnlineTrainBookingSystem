package train.booking.train.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Builder
@ToString
@Entity(name = "stations")
@NoArgsConstructor
public class Station extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId;
    @NotBlank(message = "Station Name cannot be blank")
    @Column(unique = true)
    private String stationName;

    @NotBlank(message = "Station Code cannot be blank")
    private String stationCode;


    public Station(Long stationId, String stationName, String stationCode) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
    }

    public Station(LocalDateTime createdAt, LocalDateTime updatedAt, Long stationId, String stationName, String stationCode) {
        super(createdAt, updatedAt);
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;

    }
}


package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import train.booking.train.booking.model.enums.StationType;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@Builder
@ToString
@Entity(name = "stations")
@AllArgsConstructor
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

    private String stationTag;

    @Enumerated(EnumType.STRING)
    private StationType stationType;

    public Station(Long stationId, String stationName, String stationCode, String stationTag) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.stationTag = stationTag;
    }

    public Station(LocalDateTime createdAt, LocalDateTime updatedAt, Long stationId, String stationName, String stationCode, String stationTag) {
        super(createdAt, updatedAt);
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.stationTag = stationTag;
    }
}


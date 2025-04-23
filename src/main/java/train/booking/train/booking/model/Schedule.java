package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;


@Setter
@Getter
@Builder
@ToString
@Entity(name = "schedules")
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(nullable = false)
    private LocalTime departureTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(nullable = false)
    private LocalTime arrivalTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate departureDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    private String distance;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    private Route route;

    @Column(name = "train_id", nullable = false)
    private Long trainId;
    @Column(name = "arrival-station_id", nullable = false)

    private Long arrivalStationId;
    @Column(name = "departure-station-id", nullable = false)
    private Long departureStationId;





}

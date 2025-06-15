package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.LocalDateTime;


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



    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDateTime departureDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime arrivalDate;

    @Column(nullable = false)
    private String duration;

    private String distance;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    private Route route;

    @Column(name = "train_id", nullable = false)
    private Long trainId;
    @Column(name = "arrival_station_id", nullable = false)
    private Long arrivalStationId;
    @Column(name = "departure_station_id", nullable = false)
    private Long departureStationId;







}

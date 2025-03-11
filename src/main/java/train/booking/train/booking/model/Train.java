


package train.booking.train.booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@Entity(name = "trains")
@AllArgsConstructor
@NoArgsConstructor
public class Train extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;
    private String trainCode;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;


   @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<TrainClass> trainClasses = new ArrayList<>();

    public void addTrainClass(TrainClass trainClass) {
        this.trainClasses.add(trainClass);
    }
}
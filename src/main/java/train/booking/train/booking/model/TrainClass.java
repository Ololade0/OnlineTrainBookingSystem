package train.booking.train.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder

@Entity(name = "trainclass")
@AllArgsConstructor
@NoArgsConstructor
public class TrainClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainClassId;
    @Column(name = "train_class_name")
    private String trainClassName;
//
    @Embedded
    private Fare fare;


    @JsonBackReference("train-trainClass")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    private Train train;

    @JsonManagedReference("trainClass-seats")
    @OneToMany(mappedBy = "trainClass", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Seat> seats = new ArrayList<>();

   @JsonBackReference("trainClass-bookings")
//    @JsonIgnore
    @OneToMany(mappedBy = "trainClass", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Booking> bookings;


    private int totalSeat;

    public TrainClass(Long trainClassId, String trainClassName, Object o, int i) {
    }

    @Override
    public String toString() {
        return "TrainClass{" +
                "trainClassId=" + trainClassId +
                ", className='" + trainClassName + '\'' +
                ", fare=" + fare +
                ", train=" + train +
                ", seats=" + seats +
                ", bookings=" + bookings +
                ", totalSeat=" + totalSeat +
                '}';
    }
}
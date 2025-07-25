package train.booking.train.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
public class TrainDto {
    private String trainName;
    private String trainCode;
    private Set<TrainClass> trainClasses = new HashSet<>();


}

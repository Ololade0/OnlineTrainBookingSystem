package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDto {

    private String stationCode;
    private String stationName;
    private double latitude;
    private double longitude;

}

package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.enums.StationType;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDto {

    private String stationCode;
    private String stationName;
    private String stationTag;
    private StationType stationType;
}

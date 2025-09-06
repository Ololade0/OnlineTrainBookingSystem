package train.booking.train.booking.service;


import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.Set;

public interface TrainService {
    BaseResponse newTrain(TrainDto trainDto);

    Train findTrainById(Long trainId);

    BaseResponse updateTrain(Long trainId, TrainDto trainDto);

    BaseResponse deleteTrain(Long trainId);


    Page<Train> getAllTrains(int page, int size);

    Train getTrainById(Long trainId);

    String getTrainNameById(Long id);

    Set<TrainClass> getTrainClassesInAGivenTrain(Long trainId);
}
package train.booking.train.booking.service;


import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Train;

public interface TrainService {
    BaseResponse newTrain(TrainDto trainDto);
    Train  findTrainById(Long trainId);

    BaseResponse updateTrain(Long trainId, TrainDto trainDto);
    BaseResponse deleteTrain(Long trainId);


}
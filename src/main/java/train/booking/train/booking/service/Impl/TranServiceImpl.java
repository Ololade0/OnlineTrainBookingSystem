package train.booking.train.booking.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.TrainCannotBeFoundException;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.repository.TrainRepository;
import train.booking.train.booking.service.TrainService;


@Service
@RequiredArgsConstructor
@Transactional
public class TranServiceImpl implements TrainService {



    private final TrainRepository trainRepository;

    @Override
    public BaseResponse newTrain(TrainDto trainDto) {
        Train train = Train.builder()
                .trainCode(trainDto.getTrainCode())
                .trainName(trainDto.getTrainName())
                .trainClasses(trainDto.getTrainClasses())
                .build();
          Train savedTrain =  trainRepository.save(train);
        TrainDto response = TrainDto.builder()
                .trainCode(savedTrain.getTrainCode())
                .trainClasses(savedTrain.getTrainClasses())
                .trainName(savedTrain.getTrainName())
                .build();
        return ResponseUtil.success("Train sucessfully created", response);
    }
@Override
    public Train findTrainById(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainCannotBeFoundException("Train not found"));
    }

    }





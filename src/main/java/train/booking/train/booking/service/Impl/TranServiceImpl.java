package train.booking.train.booking.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.TrainCannotBeFoundException;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.TrainRepository;
import train.booking.train.booking.service.TrainService;

import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
public class TranServiceImpl implements TrainService {



    private final TrainRepository trainRepository;

    @Override
    public BaseResponse newTrain(TrainDto trainDto) {
        verifyTrain(trainDto.getTrainName(), trainDto.getTrainClasses(), trainDto.getTrainCode());
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

    private BaseResponse verifyTrain(String trainName, Set<TrainClass> trainClasses, String trainCode) {
        if(trainRepository.existsByTrainName(trainName)){
            return ResponseUtil.inputAlreadyExist("Train Name already exist");
        }
        if (trainRepository.existsByTrainCode(trainCode)){
            return ResponseUtil.inputAlreadyExist("Train code already exist");

        }
        if (trainRepository.existsByTrainClasses(trainClasses)){
            return ResponseUtil.inputAlreadyExist("Train classes already exist");

        }
        return ResponseUtil.success("Train verification passed", null);
    }

    @Override
    public Train findTrainById(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainCannotBeFoundException("Train not found"));
    }

    @Override
    public BaseResponse updateTrain(Long trainId, TrainDto trainDto) {
        Train train = findTrainById(trainId);
        train.setTrainName(trainDto.getTrainName());
        train.setTrainCode(trainDto.getTrainCode());
        trainRepository.save(train);
        return  ResponseUtil.success("Train updated successfully", true);
    }

    @Override
    public BaseResponse deleteTrain(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainCannotBeFoundException("Train not found with id: " + trainId));
        trainRepository.delete(train);
        return ResponseUtil.success("Train deleted successfully", true);
    }

    @Override
    public Page<Train> getAllTrains(int page, int size) {
        return trainRepository.findAll(PageRequest.of(page,size));
    }

    @Override
    public Train getTrainById(Long trainId) {
      return  trainRepository.findById(trainId).orElseThrow(()
              -> new TrainCannotBeFoundException("Train not found with id: " + trainId));
    }

    @Override
    public String getTrainNameById(Long id) {
        return trainRepository.findById(id)
                .map(Train::getTrainName)
                .orElse("Unknown Train");
    }

}





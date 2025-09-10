package train.booking.train.booking.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.TrainException;
import train.booking.train.booking.exceptions.TrainClassException;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.TrainRepository;
import train.booking.train.booking.service.TrainService;

import java.util.HashSet;
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

    private void verifyTrain(String trainName, Set<TrainClass> trainClasses, String trainCode) {
        if (trainRepository.existsByTrainName(trainName)) {
            throw new TrainException("Train Name already exist");
        }

        if (trainRepository.existsByTrainCode(trainCode)) {
            throw new TrainException("Train code already exist");
        }

        if (trainClasses == null || trainClasses.isEmpty()) {
            throw new TrainClassException("Train must have at least one class");
        }
        if (trainClasses.size() != new HashSet<>(trainClasses).size()) {
            throw new TrainClassException("Duplicate train classes are not allowed");

        }

    }


    @Override
    public Train findTrainById(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainException("Train not found"));
    }


    @Override
    public BaseResponse updateTrain(Long id, TrainDto trainDto) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new TrainException("Train not found with id: " + id));

        train.setTrainCode(trainDto.getTrainCode());
        train.setTrainName(trainDto.getTrainName());
        train.setTrainClasses(trainDto.getTrainClasses());

        Train savedTrain = trainRepository.save(train);

        TrainDto response = TrainDto.builder()
                .trainCode(savedTrain.getTrainCode())
                .trainClasses(savedTrain.getTrainClasses())
                .trainName(savedTrain.getTrainName())
                .build();

        return ResponseUtil.success("Train successfully updated", response);
    }



    @Override
    public BaseResponse deleteTrain(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainException("Train not found with id: " + trainId));
        trainRepository.delete(train);
        return ResponseUtil.success("Train deleted successfully", true);
    }

    @Override
    public Page<Train> getAllTrains(int page, int size) {
        return trainRepository.findAll(PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Override
    public Train getTrainById(Long trainId) {
      return  trainRepository.findById(trainId).orElseThrow(()
              -> new TrainException("Train not found with id: " + trainId));
    }

    @Override
    public String getTrainNameById(Long id) {
        return trainRepository.findById(id)
                .map(Train::getTrainName)
                .orElse("Unknown Train");
    }

    @Override
    public Set<TrainClass> getTrainClassesInAGivenTrain(Long trainId) {
        Train foundTrain = trainRepository.findById(trainId).orElseThrow(() ->
                new TrainClassException("Train not found"));
        return foundTrain.getTrainClasses();
    }

}





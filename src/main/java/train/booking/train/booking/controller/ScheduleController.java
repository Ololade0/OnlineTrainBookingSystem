package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.ScheduleResponse;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.service.ScheduleService;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/schedule")
@RequiredArgsConstructor

public class ScheduleController {

    private final ScheduleService scheduleService;


   @PostMapping("create-schedule")
    public ResponseEntity<BaseResponse> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        log.info("Received request: {}", scheduleDTO);
        BaseResponse response = scheduleService.newSchedule(scheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("find-schedule/{scheduleId}")
    public ResponseEntity<?> findSchedule(@PathVariable Long scheduleId){
       Schedule foundSchedule = scheduleService.findSchedulesById(scheduleId);
       return ResponseEntity.ok(foundSchedule);

    }
//
//    @GetMapping("find-schedules/{scheduleId}")
//    public ResponseEntity<?> findSchedules(@PathVariable Long scheduleId){
//        BaseResponse foundSchedule = scheduleService.findScheduleById(scheduleId);
//        return ResponseEntity.ok(foundSchedule);
//
//    }



    @PostMapping("/find")
    public ResponseEntity<?> findSchedule(@RequestBody ScheduleDTO findScheduleDTO) {
        try {
            ScheduleResponse scheduleResponse = scheduleService.findSchedule(findScheduleDTO);
            return ResponseEntity.ok(scheduleResponse);
        } catch (ScheduleCannotBeFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }






}

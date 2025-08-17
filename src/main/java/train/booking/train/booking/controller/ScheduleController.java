package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.service.ScheduleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<?> findScheduleById(@PathVariable Long scheduleId){
       Schedule foundSchedule = scheduleService.findSchedulesById(scheduleId);
       return ResponseEntity.ok(foundSchedule);

    }


    @GetMapping("/find-schedule")
    public ResponseEntity<?> findSchedule(@RequestParam Long departureId,
                                          @RequestParam Long arrivalStationId,
                                          @RequestParam LocalDate departureDate) {
        try {
            ScheduleResponse scheduleResponse = scheduleService.findSchedule(departureId, arrivalStationId, departureDate);
            return ResponseEntity.ok(scheduleResponse);
        } catch (ScheduleCannotBeFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("find-all-schedules")
    public ResponseEntity<?>findAllSchedules(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size){
        Page<Schedule>schedulePage = scheduleService.findAllSchedules(page, size);
       return ResponseEntity.ok(schedulePage);


    }

    @PutMapping("/update-schedule/{id}")
    public ResponseEntity<BaseResponse> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO) {
        BaseResponse response = scheduleService.updateSchedule(id, scheduleDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteSchedule(@PathVariable Long id) {
        BaseResponse response = scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/schedule-route")
    public ResponseEntity<?> getScheduleTimeTable(@RequestParam(value = "route", required = false) Route route) {
        if (route == null) {
            return ResponseEntity.badRequest().body("Route parameter is required.");
        }

        try {
            List<Schedule> schedules = scheduleService.findByRouteName(route);
            return ResponseEntity.ok(schedules);
        } catch (ScheduleCannotBeFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }


}






package train.booking.train.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.SignUpRequest;
import train.booking.train.booking.dto.response.SignUpUserResponse;
import train.booking.train.booking.model.Role;
import train.booking.train.booking.service.RoleService;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/role")
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role){
        Role savedRole = roleService.save(role);
        log.info("Incoming user payload: {}", savedRole);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

}

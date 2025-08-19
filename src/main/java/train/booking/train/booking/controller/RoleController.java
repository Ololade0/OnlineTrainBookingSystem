package train.booking.train.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.RoleDTo;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.service.RoleService;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create-role")
    public ResponseEntity<?> createRole(@RequestBody RoleDTo roleDTo){
        return  ResponseEntity.ok(roleService.save((roleDTo)));
    }
    @GetMapping("/get-all-roles")
    public ResponseEntity<List<RoleType>> getAllRoles() {
        List<RoleType> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestBody RoleDTo roleDTo) {
        BaseResponse response = roleService.update(roleDTo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{roleType}")
    public ResponseEntity<?> deleteRole(@PathVariable RoleType roleType) {
        BaseResponse response = roleService.delete(roleType);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

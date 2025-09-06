package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.GenderType;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.service.AdminService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/admin")
public class AdminController {

    private final AdminService adminService;


    @PostMapping("/register-superadmin")
    public ResponseEntity<BaseResponse> superAdminSignUp(@RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {
        BaseResponse response = adminService.superAdminSignUp(signUpRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/find-roles")
    public ResponseEntity<?> getAllByRoles(@RequestParam RoleType roleType,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "50") int size) {
        Page<FindAllByRolesDTO> listOfAdmin = adminService.findAllByRole(roleType, page, size);
        return new ResponseEntity<>(listOfAdmin.getContent(), HttpStatus.OK);
    }

    @GetMapping("/get-all-identificationTypes")
    public ResponseEntity<List<IdentificationType>> getAllIdenticationTypes() {
        List<IdentificationType> identificationTypes = adminService.getAllIdenticationTypes();
        return ResponseEntity.ok(identificationTypes);
    }

    @GetMapping("/get-all-genders")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<List<GenderType>> genderType() {
        List<GenderType> allGenders = adminService.getAllGenders();
        return ResponseEntity.ok(allGenders);
    }

    @GetMapping("/get-all-ageRange")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<List<AgeRange>> ageRange() {
        List<AgeRange> ageRangeList = adminService.getAllAgeRange();
        return ResponseEntity.ok(ageRangeList);
    }




}





package train.booking.train.booking.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.model.enums.RoleType;
import train.booking.train.booking.service.AdminService;

import javax.management.relation.RoleNotFoundException;

@Controller
@Slf4j
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
public class AdminController {
        private final AdminService adminService;


    @PostMapping("/register-superadmin")
    public ResponseEntity<?> superAdminSignUp(@Valid @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {;
        return new ResponseEntity<>(adminService.superAdminSignUp(signUpRequest), HttpStatus.CREATED);
    }

    @GetMapping("/register-superadmin")
    public String showAdminRegisterPage(Model model) {
        model.addAttribute("admin", new UserDTO());
        return "register-admin";
    }

    @GetMapping("/find-roles")
    public ResponseEntity<?> getAllByRoles(@RequestParam RoleType roleType,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam (defaultValue = "50")int size){
      Page<FindAllByRolesDTO> listOfAdmin =  adminService.findAllByRole(roleType, page, size);
        return new ResponseEntity<>(listOfAdmin.getContent(), HttpStatus.OK);
    }




}

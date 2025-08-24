package train.booking.train.booking.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseCodes;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.model.User;
import train.booking.train.booking.service.UserService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated() and hasRole('SUPERADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) throws UnirestException, RoleNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated userssss: " + auth.getName());
        System.out.println("Authoritiessssss: " + auth.getAuthorities());

        return new ResponseEntity<>(userService.signUpNewUser(signUpRequest), HttpStatus.CREATED);
    }

    @GetMapping("get-user/{userId}")
    @PreAuthorize("isAuthenticated() and hasRole('SUPERADMIN')")
    public ResponseEntity<?> findUserById(@PathVariable Long userId) {
        User foundUser = userService.findUserById(userId);
        return ResponseEntity.ok(foundUser);
    }


    @GetMapping("/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        User foundUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(foundUser);
    }

    @PutMapping("update-user-profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        ;
        return ResponseEntity.ok(userService.updateUserProfile(userDTO, userId));

    }


    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, Model model) {
        try {
            String result = userService.activateAccount(token);
            model.addAttribute("message", result);
            return "activation-success";
        } catch (RuntimeException | UnirestException e) {
            model.addAttribute("message", e.getMessage());
            return "activation-failed";
        }

    }

    // ✅ Only authenticated + must have SUPERADMIN_ROLE
    @GetMapping("/getAllNonUserAccounts")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public BaseResponse getAllNonUserAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {

        Page<User> accounts = userService.getAllNonUserAccounts(page, size);
        return ResponseUtil.response(
                ResponseCodes.REQUEST_SUCCESSFUL,
                "Accounts retrieved successfully",
                accounts
        );
    }
    @GetMapping("/searchUsers")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam String query,
                                         @RequestParam(defaultValue = "0")int page,
                                         @RequestParam(defaultValue = "50")int size
                                         ){
        Page<User> searchedUsers = userService.searchUsers(query, page, size);
        if (searchedUsers.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No matching user found"));
        }

        return ResponseEntity.ok(searchedUsers);
    }

    @GetMapping("/delete-user/{userId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
       String deletedUser = userService.deleteUser(userId);
       return ResponseEntity.ok(deletedUser);

    }


    }







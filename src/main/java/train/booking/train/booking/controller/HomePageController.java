package train.booking.train.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController {


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        System.out.println("Health check ping received at " + java.time.LocalDateTime.now());

        return ResponseEntity.ok("App is alive");
    }
}

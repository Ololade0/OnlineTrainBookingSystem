package train.booking.train.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/auth/home")
public class HomePageController {

    @GetMapping("/home-page")
    public String homePage()
    {
        return "home-page";
    }
}

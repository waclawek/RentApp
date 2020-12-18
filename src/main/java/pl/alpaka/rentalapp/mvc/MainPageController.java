package pl.alpaka.rentalapp.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    @GetMapping("/")
    public String mainPage() {
        return "main.html";
    }
}
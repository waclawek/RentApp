package pl.alpaka.rentalapp.mvc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserDto;
import pl.alpaka.rentalapp.domain.user.UserService;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
    public class RegisterController {
    private final UserService userService;

    @GetMapping
    ModelAndView registerPage() {
        ModelAndView modelAndView = new ModelAndView("registration.html");
        modelAndView.addObject("userDto", new UserDto());
        return modelAndView;
    }

}
package pl.alpaka.rentalapp.mvc.daschboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.UserService;

@Controller
@SessionAttributes("username")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    ModelAndView currUser(Authentication authentication) {
        String username;
        try {
            username = authentication.getName();
        }catch (NullPointerException ex){
            return new ModelAndView("login.html");
        }
        ModelAndView modelAndView = new ModelAndView("dashboard.html");
        if (userService.getAll().stream().anyMatch(p -> p.getUsername().equals(username))) {
            modelAndView.addObject("user", userService.getByUsername(username));
        }
        return modelAndView;
    }
}
package pl.alpaka.rentalapp.mvc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserDto;
import pl.alpaka.rentalapp.domain.user.UserRepository;
import pl.alpaka.rentalapp.domain.user.UserService;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    ModelAndView registerPage() {
        ModelAndView modelAndView = new ModelAndView("registration.html");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping
    ModelAndView registerUser(@ModelAttribute @Valid UserDto userDto, BindingResult bindingResult, @RequestParam(name = "password2", required = false) String password2) {
        ModelAndView model = new ModelAndView("registration.html");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            model.addObject("errors", errors);
            return model;
        }
         else {
            try{
                userService.registerOwner(userDto);
            }catch (IllegalArgumentException ex){
                model.addObject("message", ex.getMessage());
                System.out.println(ex.getMessage());
                return model;
            }
            model = new ModelAndView("login.html");
            model.addObject("message", "Verification email has been sent to Your mailbox. Click link to activate");
            return model;
        }
    }

    @GetMapping
    @PostMapping
    @RequestMapping(path = "/profile")
    ModelAndView editTask(@ModelAttribute User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ModelAndView mav = new ModelAndView("editProfile.html");
        User userToEdit = userService.getByUsername(username);
        mav.addObject("user", userToEdit);
        return mav;
    }

    @PostMapping
    @RequestMapping(path = "/save", method = RequestMethod.POST)
    String editUserSaveChanges(@ModelAttribute User user, @RequestParam(value = "password2", required = false) String password2, @RequestParam(value = "password", required = false) String password) {
        if (password.isEmpty() && password2.isEmpty()) {
            userService.updateProfile(user);
            return "redirect:/system";
        }
        if (password.equals(password2) && !password.isEmpty()) {
            user.setPassword(password);
            userService.updateProfile(user);
            return "redirect:/system";
        } else
            return "redirect:/user/profile";
    }
}
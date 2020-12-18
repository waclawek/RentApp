package pl.alpaka.rentalapp.mvc.user;

import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationToken;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;

    @GetMapping
    String loginPage() {
        return "login.html";
    }

    @GetMapping(path = "/error")
    ModelAndView loginWithErrorPage(HttpServletRequest request, Model model) {
        ModelAndView mav = new ModelAndView("login.html");
        HttpSession session = request.getSession();
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        mav.addObject("errorMessage", errorMessage);
        return mav;
    }

    @GetMapping
    @RequestMapping(path = "/forgotten-password")
    ModelAndView forgotPassword() {
        return new ModelAndView("forgottenPassword");
    }

    @PostMapping
    @RequestMapping(path = "/forgotten-password-email")
    ModelAndView sendEmailForPasswordReset(@RequestParam(name = "email") String email) {
        ModelAndView mav = new ModelAndView("forgottenPassword");
        Optional<User> userOptional = userService.findByUsername(email);
        if (userOptional.isEmpty()) {
            mav.addObject("errorMessage", "No email in database, try again.");
            return mav;
        }
        if(!userOptional.get().isEnabled()){
            mav.addObject("errorMessage", "User is not yet activated. Activation link has been resented.");
            return mav;
        }
        userService.resetPasswordFirstStep(email);
        mav.addObject("message", "Email with reset password link has been sent.");
        return mav;
    }

    @GetMapping(path = "/resetPassword")
    public ModelAndView activateUser(@RequestParam(name = "token") String token){
        ModelAndView mav = new ModelAndView("forgottenPasswordReset");
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken.isPresent()){
            User user = userService.getByUsername(verificationToken.get().getUsername());
            if(verificationToken.get().getExpiryDate().isAfter(LocalDateTime.now())){
                mav.addObject("username", user.getUsername());
            }else {
                userService.resetPasswordFirstStep(user.getEmail());
                mav.addObject("message", "Verification link has expired, new link was sent.");
            }
            verificationTokenRepository.delete(verificationToken.get());
            return mav;
        }
        mav.addObject("message", "No activation token has been found for this link. Verify url.");
        return mav;
    }

    @PostMapping(path = "/new-password")
    ModelAndView newPassword(@RequestParam(name = "username") String username,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "matchingPassword") String matchingPassword){
        if(password.equals(matchingPassword)){
            User user = userService.findByUsername(username).get();
            userService.resetPasswordSecondStep(user, password);
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("message", "Password successfully changed.");
            return mav;
        }
        ModelAndView mav = new ModelAndView("forgottenPasswordReset");
        mav.addObject("username", username);
        mav.addObject("errorMessage", "Passwords do not match.");
        return mav;
    }
}

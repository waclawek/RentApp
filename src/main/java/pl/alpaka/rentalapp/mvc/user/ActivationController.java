package pl.alpaka.rentalapp.mvc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationToken;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenRepository;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping(path = "/activate")
@RequiredArgsConstructor
public class ActivationController {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final UserService userService;

    @GetMapping
    public ModelAndView activateUser(@RequestParam(name = "token") String token){
        ModelAndView mav = new ModelAndView("login.html");
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken.isPresent()){
            User user = userService.getByUsername(verificationToken.get().getUsername());
            if(user.isEnabled()){
                mav.addObject("message", "User is already active Log in.");
                deleteToken(verificationToken);
                return mav;
            }
            if(verificationToken.get().getExpiryDate().isAfter(LocalDateTime.now())){
                user.setEnabled(true);
                userService.update(user);
                mav.addObject("message", "User has been activated. Log in.");
            }else {
                verificationTokenService.createVerificationToken(user);
                mav.addObject("message", "Verification link has expired, new link was sent.");
            }
            deleteToken(verificationToken);
            return mav;
        }
        mav.addObject("message", "No activation token has been found for this link. Verify url.");
        return mav;
    }

    private void deleteToken(Optional<VerificationToken> verificationToken) {
        verificationTokenRepository.delete(verificationToken.get());
    }
}

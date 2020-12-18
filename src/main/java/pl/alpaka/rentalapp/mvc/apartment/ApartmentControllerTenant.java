package pl.alpaka.rentalapp.mvc.apartment;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/tenant/apartment")
public class ApartmentControllerTenant {

    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    ModelAndView getApartments() {
        ModelAndView mav = new ModelAndView("apartment.html");
        String username = getUsername(mav);
        List<Apartment> apartmentList = apartmentService.getAllByTenantUsername(username);
        mav.addObject("apartments", apartmentList);



        return mav;
    }

    private String getUsername(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (userService.getByUsername(username).getUsername().equals(username)) {
            mav.addObject("user", userService.getByUsername(username));
        }
        return username;
    }


}

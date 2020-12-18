package pl.alpaka.rentalapp.mvc.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.rent.RentService;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/tenant/rent")
public class RentControllerTenant {

    private final RentService rentService;
    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('RENTIER')")
    ModelAndView tenantRentsPage() {
        ModelAndView mav = new ModelAndView("rent.html");
        //toDo addObject - balance of each rent
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Rent> userRents = rentService.getRentsByTenantUsername(username);
        mav.addObject("rents", userRents);
        mav.addObject("apartmentService", apartmentService);
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return mav;
    }
}

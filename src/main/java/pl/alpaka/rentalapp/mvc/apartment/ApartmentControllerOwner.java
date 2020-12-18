package pl.alpaka.rentalapp.mvc.apartment;

import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/owner/apartment")
public class ApartmentControllerOwner {

    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping(path = "/all")
        //toDo add admin authorisation
    ModelAndView getAllApartments() {
        ModelAndView mav = new ModelAndView("apartment.html");
        mav.addObject("apartments", apartmentService.getAll());
        return mav;
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView getAllUserApartments() {
        ModelAndView mav = new ModelAndView("apartment.html");
        List<Apartment> apartmentList = apartmentService.getAllByOwnerUsername(getCurrentUsername());
        mav.addObject("apartments", apartmentList);
        getUsername(mav);
        return mav;
    }

    private void getUsername(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
    }

    @GetMapping(path = "/create")
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView createApartment() {
        ModelAndView mav = new ModelAndView("createApartment.html");
        Apartment newApartment = new Apartment();
        mav.addObject("apartment", newApartment);
        getUsername(mav);
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    String addApartmentPage(@ModelAttribute Apartment apartment) {
        apartmentService.createNewApartment(apartment);
        return "redirect:/owner/apartment";
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/delete")
    ModelAndView deleteApartment(@RequestParam(value = "apartmentId") Integer apartmentId) {
        Apartment apartmentToBeDeleted = apartmentService.findById(apartmentId);
        List<Apartment> ownerApartmentList = apartmentService.getAllByOwnerUsername(getCurrentUsername());
        if (ownerApartmentList.contains(apartmentToBeDeleted)) {
            ModelAndView mav = new ModelAndView("deleteApartment.html");
            mav.addObject("apartment", apartmentToBeDeleted);
            getUsername(mav);
            return mav;
        }
        return getAllUserApartments();
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/deleteApartment")
    String deleteApartmentById(@RequestParam(value = "apartmentId") Integer apartmentId) {
        apartmentService.deleteById(apartmentId);
        return "redirect:/owner/apartment";
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/edit")
    ModelAndView editApartment(@RequestParam(value = "apartmentId") Integer apartmentId) {
        Apartment apartmentToEdit = apartmentService.findById(apartmentId);
        List<Apartment> ownerApartmentList = apartmentService.getAllByOwnerUsername(getCurrentUsername());
        if (ownerApartmentList.contains(apartmentToEdit)) {
            ModelAndView mav = new ModelAndView("editApartment.html");
            mav.addObject("apartment", apartmentToEdit);
            getUsername(mav);
            return mav;
        }
        return getAllUserApartments();
    }

    @PostMapping
    @RequestMapping(path = "/editApartment")
    @PreAuthorize("hasRole('OWNER')")
    String editApartmentUpdate(@ModelAttribute Apartment apartment) {
        Set<User> owners = new HashSet<>();
        owners.add(userService.getByUsername(getCurrentUsername()));
        apartment.setOwners(owners);
        apartment.setTaskList(new LinkedList<>());
        apartmentService.update(apartment);
        return "redirect:/owner/apartment";
    }


    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}



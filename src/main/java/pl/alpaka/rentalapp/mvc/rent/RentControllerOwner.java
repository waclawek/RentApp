package pl.alpaka.rentalapp.mvc.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.rent.RentService;
import pl.alpaka.rentalapp.domain.user.TenantDto;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserDto;
import pl.alpaka.rentalapp.domain.user.UserService;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("owner/rent")
public class RentControllerOwner {

    private final RentService rentService;
    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView ownerRentPage() {
        ModelAndView mav = new ModelAndView("rent.html");
        //toDo addObject - balance of each rent
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Rent> userRents = rentService.getRentsByOwnerUsername(username);
        mav.addObject("rents", userRents);
        mav.addObject("apartmentService", apartmentService);
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/tenants")
    ModelAndView rentTenants(@RequestParam(value = "rentId") Integer rentId) {
        ModelAndView mav = tenantsPageLoad(rentId);
        findUsername(mav);
        return mav;
    }

    private ModelAndView rentTenantsWithErrors(Integer rentId, BindingResult bindingResult) {
        ModelAndView mav = tenantsPageLoad(rentId);
        findUsername(mav);
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors()
                    .stream().map(err -> err.getDefaultMessage())
                    .collect(Collectors.toList());
            mav.addObject("errors", errors);
        }
        return mav;
    }

    private ModelAndView tenantsPageLoad(Integer rentId) {
        ModelAndView mav = new ModelAndView("tenantsFromRent.html");
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        mav.addObject("apartmentService", apartmentService);
        List<User> tenants = rent.getTenants();
        mav.addObject("tenants", tenants);
        TenantDto tenantDto = new TenantDto();
        mav.addObject("tenantDto", tenantDto);
        return mav;
    }

    private void findUsername(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/newTenant")
    ModelAndView newTenant(@ModelAttribute @Valid TenantDto tenantDto, BindingResult bindingResult, @RequestParam(value = "rentId") Integer rentId) {
        if(bindingResult.hasErrors()){
            return rentTenantsWithErrors(rentId, bindingResult);
        }
        User tenant = User.builder()
                .firstname(tenantDto.getFirstname())
                .lastname(tenantDto.getLastname())
                .telephone(tenantDto.getTelephone())
                .email(tenantDto.getEmail())
                .build();

        rentService.checkNewTenant(tenant, rentId);
        return rentTenants(rentId);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/tenants/edit")
    ModelAndView editTenant(@RequestParam(value = "tenantUsername") String username, @RequestParam(value = "rentId") Integer rentId) {
        ModelAndView mav = new ModelAndView("editTenant.html");
        User tenant = userService.getByUsername(username);
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        mav.addObject("tenant", tenant);
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return mav;
    }

    //todo check!!!! aftwer merge
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/tenants/editTenant")
    ModelAndView updateTenant(@RequestParam(value = "tenantUsername") String username,
                              @ModelAttribute User tenant,
                              @RequestParam(value = "rentId") Integer rentId,
                              @RequestParam(value = "password1") String password1,
                              @RequestParam(value = "password2") String password2) {
        //todo refactor user to userDto
        if (!password1.isEmpty() && !password2.isEmpty()) {
            if (password1.equals(password2)) {
                tenant.setPassword(password1);
            }else {
                throw new InvalidParameterException("Passwords do not match");
            }
        }
        tenant.setUsername(username);
        rentService.updateTenant(tenant, username);
        return rentTenants(rentId);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/tenants/delete")
    ModelAndView deleteTenant(@RequestParam(value = "tenantUsername") String username, @RequestParam(value = "rentId") Integer rentId) {
        ModelAndView mav = new ModelAndView("deleteTenant.html");
        User tenantToDelete = userService.getByUsername(username);
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        mav.addObject("tenant", tenantToDelete);
        mav.addObject("apartmentService", apartmentService);
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/tenants/deleteTenantFromRent")
    ModelAndView deleteTenantFromRent(@RequestParam(value = "tenantUsername") String username, @RequestParam(value = "rentId") Integer rentId) {
        rentService.deleteTenantFromRent(username, rentId);
        return rentTenants(rentId);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/edit")
    ModelAndView editRent(@RequestParam(value = "rentId") Integer rentId) {
        Rent rentToEdit = rentService.getOneById(rentId);
        ModelAndView mav = new ModelAndView("editRent.html");
        mav.addObject("rent", rentToEdit);
        mav.addObject("apartmentService", apartmentService);
        mav.addObject("apartments", apartmentService.getAllByOwnerUsername(getCurrentUsername()));
        findUsername(mav);
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/editRent")
    String editRentStageTwo(@ModelAttribute Rent rent, BindingResult bindingResult) {
        Rent rentToEdit = rentService.getOneById(rent.getId());
        rent.setTenants(rentToEdit.getTenants());
        System.out.println(bindingResult.getAllErrors().toString());
        rentService.update(rent);
        return "redirect:/owner/rent";
    }

    @GetMapping(path = "/create")
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView createRent() {
        ModelAndView mav = new ModelAndView("createRent.html");
        Rent rent = new Rent();
        mav.addObject("rent", rent);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Apartment> apartments = apartmentService.getAllByOwnerUsername(username);
        mav.addObject("apartments", apartments);
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/delete")
    ModelAndView deleteRent(@RequestParam(value = "rentId") Integer rentId) {
        ModelAndView mav = new ModelAndView("deleteRent.html");
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        mav.addObject("apartmentService", apartmentService);
        if (userService.getByUsername(getCurrentUsername()).getUsername().equals(getCurrentUsername()))
            mav.addObject("user", userService.getByUsername(getCurrentUsername()));
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/deleteRent")
    ModelAndView deleteRent2(@RequestParam(value = "rentId") Integer rentId) {
        rentService.deleteById(rentId);
        return ownerRentPage();
    }
    @PostMapping
    @RequestMapping(path = "/createRent")
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView createNewRent(@ModelAttribute @Valid Rent rent, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("createRent.html");
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).collect(Collectors.toList());
            mav.addObject("errors", errors);
            if (userService.getByUsername(getCurrentUsername()).getUsername().equals(getCurrentUsername()))
                mav.addObject("user", userService.getByUsername(getCurrentUsername()));
            List<Apartment> apartments = apartmentService.getAllByOwnerUsername(getCurrentUsername());
            mav.addObject("apartments", apartments);
            return mav;
        }
        try{
            rentService.create(rent);
        }catch (InvalidParameterException ex){
            mav.addObject("message", ex.getMessage());
            if (userService.getByUsername(getCurrentUsername()).getUsername().equals(getCurrentUsername()))
                mav.addObject("user", userService.getByUsername(getCurrentUsername()));
            List<Apartment> apartments = apartmentService.getAllByOwnerUsername(getCurrentUsername());
            mav.addObject("apartments", apartments);
            return mav;
        }
        return ownerRentPage();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

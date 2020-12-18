package pl.alpaka.rentalapp.mvc.payment;

import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.payment.Payment;
import pl.alpaka.rentalapp.domain.payment.PaymentService;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.rent.RentService;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/tenant/payment")

public class PaymentControllerTenant {

    private final PaymentService paymentService;
    private final RentService rentService;
    private final UserService userService;
    private final ApartmentService apartmentService;

    @GetMapping
    ModelAndView getPaymentPage() {
        ModelAndView mav = new ModelAndView("payment.html");
        String username = getUsername(mav);
        List<Payment> paymentList = paymentService.getPaymentsByTenant(username);
        mav.addObject("payments", paymentList);
        List<Rent> rents = rentService.getRentsByTenantUsername(username);
        mav.addObject("rents", rents);
        BigDecimal balance = paymentService.findBalanceByTenantUsername(username);
        mav.addObject("balance", balance);
        return mav;
    }

    @PostMapping
    @RequestMapping(path = "/byrent")
    ModelAndView getPaymentByRent(@RequestParam(value = "rentIdForPayment") int rentId){
        ModelAndView mav = new ModelAndView("paymentByRent.html");
        String username = getUsername(mav);
        List<Payment> paymentList = paymentService.getPaymentsByRent(rentId);
        mav.addObject("payments", paymentList);
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        Apartment apartment = apartmentService.findById(rent.getApartmentId());
        mav.addObject("apartment", apartment);
        BigDecimal balanceByRent = paymentService.findBalanceByRentId(rentId);
        mav.addObject("balance", balanceByRent);
        return mav;
    }

    private String getUsername(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if(userService.getByUsername(username).getUsername().equals(username)){
            mav.addObject("user", userService.getByUsername(username));
        }
        return username;
    }

}

package pl.alpaka.rentalapp.mvc.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.payment.Payment;
import pl.alpaka.rentalapp.domain.payment.PaymentService;
import pl.alpaka.rentalapp.domain.payment.PaymentType;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.rent.RentService;
import pl.alpaka.rentalapp.domain.user.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/owner/payment")
public class PaymentControllerOwner {

    private final PaymentService paymentService;
    private final RentService rentService;
    private final UserService userService;
    private final ApartmentService apartmentService;

    @GetMapping
    ModelAndView getPaymentPage() {
        ModelAndView mav = new ModelAndView("payment.html");
        String username = getName(mav);
        List<Payment> paymentList = paymentService.getPaymentsByOwner(username);
        mav.addObject("payments", paymentList);
        Payment payment = new Payment();
        mav.addObject("payment", payment);
        List<Rent> rentList = rentService.getRentsByOwnerUsername(username);
        mav.addObject("rents", rentList);
        BigDecimal balance = paymentService.findBalanceByOwnerUsername(username);
        mav.addObject("balance", balance);
        BigDecimal earningsThisYear = paymentService.findCurrentYearEarningsByOwnerUsername(username);
        mav.addObject("earningsThisYear", earningsThisYear);
        BigDecimal depositsTotal = paymentService.findSumOfDepositsByOwnerUsername(username);
        mav.addObject("depositsTotal", depositsTotal);
        return mav;
    }

    private String getName(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (userService.getByUsername(username).getUsername().equals(username))
            mav.addObject("user", userService.getByUsername(username));
        return username;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    ModelAndView createPayment(@ModelAttribute @Valid Payment payment, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("payment.html");
        String username = getName(mav);
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).collect(Collectors.toList());
            mav.addObject("errors", errors);
            List<Payment> paymentList = paymentService.getPaymentsByOwner(username);
            mav.addObject("payments", paymentList);
            List<Rent> rentList = rentService.getRentsByOwnerUsername(username);
            mav.addObject("rents", rentList);
            return mav;
        }
        if (payment.getPaymentType().equals(PaymentType.PAYMENT)) {
            payment.setPaid(true);
        } else {
            payment.setPaid(false);
        }
        payment.setCreated(LocalDate.now());
        paymentService.create(payment);
        return getPaymentPage();
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping("/byrent")
    ModelAndView getPaymentsByRent(@RequestParam(value = "rentIdForPayment") int rentId) {
        ModelAndView mav = new ModelAndView("paymentByRent.html");
        String username = getName(mav);
        List<Payment> paymentList = paymentService.getPaymentsByRent(rentId);
        mav.addObject("payments", paymentList);
        Payment payment = new Payment();
        payment.setRentId(rentId);
        mav.addObject("payment", payment);
        Rent rent = rentService.getOneById(rentId);
        mav.addObject("rent", rent);
        Apartment apartment = apartmentService.findById(rent.getApartmentId());
        mav.addObject("apartment", apartment);
        mav.addObject("rentIdForPayment", rentId);
        BigDecimal balanceByRent = paymentService.findBalanceByRentId(rentId);
        mav.addObject("balance", balanceByRent);
        BigDecimal depositFromRent = paymentService.findDepositByRent(rentId);
        mav.addObject("depositFromRent", depositFromRent);
        return mav;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping("/byrent/new-payment")
    ModelAndView addPaymentsByRent(@RequestParam(value = "rentIdForPayment") int rentId, @ModelAttribute @Valid Payment payment, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("paymentByRent.html");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            String username = getName(mav);
            mav.addObject("errors", errors);
            List<Payment> paymentList = paymentService.getPaymentsByRent(rentId);
            mav.addObject("payments", paymentList);
            Rent rent = rentService.getOneById(rentId);
            mav.addObject("rent", rent);
            Apartment apartment = apartmentService.findById(rent.getApartmentId());
            mav.addObject("apartment", apartment);
        }
        payment.setCreated(LocalDate.now());
        paymentService.create(payment);
        return getPaymentsByRent(rentId);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @RequestMapping(path = "/delete")
    ModelAndView deletePayment(@RequestParam(value = "paymentId") Integer paymentId) {
        paymentService.deleteById(paymentId);
        return getPaymentPage();
    }


}

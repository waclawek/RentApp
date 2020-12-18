package pl.alpaka.rentalapp.domain.rent;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.alpaka.rentalapp.domain.payment.Payment;
import pl.alpaka.rentalapp.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    private int id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private List<Payment> paymentList = new LinkedList<>();
    private int apartmentId;
    private List<User> tenants;
}

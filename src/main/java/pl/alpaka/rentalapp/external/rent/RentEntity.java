package pl.alpaka.rentalapp.external.rent;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.alpaka.rentalapp.domain.payment.Payment;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;
import pl.alpaka.rentalapp.external.payment.PaymentEntity;
import pl.alpaka.rentalapp.external.user.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rents")
public class RentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate beginDate;
    private LocalDate endDate;
    @OneToMany
    private List<PaymentEntity> paymentList;
    @ManyToOne
    private ApartmentEntity apartmentEntity;
    @ManyToMany
    private List<UserEntity> tenants;
    private String name;
}

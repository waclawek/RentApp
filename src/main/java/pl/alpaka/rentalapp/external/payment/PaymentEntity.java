package pl.alpaka.rentalapp.external.payment;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.alpaka.rentalapp.domain.payment.PaymentType;
import pl.alpaka.rentalapp.external.rent.RentEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "rent_id")
    private RentEntity rentEntity;
    private String description;
    private BigDecimal amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDeadline;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;
    private boolean paid;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

}

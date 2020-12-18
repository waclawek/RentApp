package pl.alpaka.rentalapp.external.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.alpaka.rentalapp.domain.payment.Payment;
import pl.alpaka.rentalapp.domain.payment.PaymentRepository;
import pl.alpaka.rentalapp.domain.payment.PaymentType;
import pl.alpaka.rentalapp.external.rent.JpaRentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabasePaymentRepository implements PaymentRepository {

    private final JpaPaymentRepository paymentRepository;
    private final JpaRentRepository rentRepository;

    @Override
    public void create(Payment payment) {
        paymentRepository.save(mapToExternal(payment));
    }

    @Override
    public Optional<Payment> getPaymentById(int id) {
        return paymentRepository.findById(id).map(mapToDomain());
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll().stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public List<Payment> getPaymentsByRentId(int rentId) {
        return paymentRepository.getPaymentsByRentId(rentId).stream()
                .map(mapToDomain())
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> getPaymentsByOwner(String username) {
        return paymentRepository.getPaymentsByOwner(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public BigDecimal findBalanceByOwnerUsername(String username) {
        List<Payment> paymentList = getPaymentsByOwner(username);
        BigDecimal balance = BigDecimal.ZERO;

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.PAYMENT)) {
                balance = balance.add(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.BILL)) {
                balance = balance.subtract(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.RENT)) {
                balance = balance.subtract(p.getAmount());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal findCurrentYearEarningsByOwnerUsername(String username) {
        List<Payment> paymentList = getPaymentsByOwner(username);
        BigDecimal currentYearEarnings = BigDecimal.ZERO;

        if (paymentList.isEmpty()) {
            return currentYearEarnings;
        }

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.PAYMENT) && p.getCreated().getYear() == LocalDate.now().getYear()) {
                currentYearEarnings = currentYearEarnings.add(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.BILL)) {
                currentYearEarnings = currentYearEarnings.subtract(p.getAmount());
            }
        }
        return currentYearEarnings;
    }

    @Override
    public BigDecimal findSumOfDepositsByOwnerUsername(String username) {
        List<Payment> paymentList = getPaymentsByOwner(username);
        BigDecimal depositsSum = BigDecimal.ZERO;

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.DEPOSIT)) {
                depositsSum = depositsSum.add(p.getAmount());
            }
        }
        return depositsSum;
    }

    @Override
    public List<Payment> getPaymentsByTenant(String username) {
        return paymentRepository.getPaymentsByTenant(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public BigDecimal findBalanceByTenantUsername(String username) {
        List<Payment> paymentList = getPaymentsByTenant(username);
        BigDecimal balance = BigDecimal.ZERO;

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.PAYMENT)) {
                balance = balance.add(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.BILL)) {
                balance = balance.subtract(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.RENT)) {
                balance = balance.subtract(p.getAmount());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal findBalanceByRentId(int rentId) {
        List<Payment> paymentList = getPaymentsByRentId(rentId);
        BigDecimal balance = BigDecimal.ZERO;

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.PAYMENT)) {
                balance = balance.add(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.BILL)) {
                balance = balance.subtract(p.getAmount());
            }
            if (p.getPaymentType().equals(PaymentType.RENT)) {
                balance = balance.subtract(p.getAmount());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal findDepositByRent(int rentId) {
        List<Payment> paymentList = getPaymentsByRentId(rentId);
        BigDecimal deposits = BigDecimal.ZERO;

        for (Payment p : paymentList) {
            if (p.getPaymentType().equals(PaymentType.DEPOSIT)) {
                deposits = deposits.add(p.getAmount());
            }
        }
        return deposits;
    }

    @Override
    public void update(Payment payment) {
        paymentRepository.save(mapToExternal(payment));
    }

    @Override
    public void deleteById(int id) {
        paymentRepository.deleteById(id);
    }

    public static Function<PaymentEntity, Payment> mapToDomain() {
        return paymentEntity -> new Payment().builder()
                .id(paymentEntity.getId())
                .amount(paymentEntity.getAmount())
                .description(paymentEntity.getDescription())
                .paid(paymentEntity.isPaid())
                .paymentDeadline(paymentEntity.getPaymentDeadline())
                .rentId(paymentEntity.getRentEntity().getId())
                .paymentType(paymentEntity.getPaymentType())
                .created(paymentEntity.getCreated())
                .build();
    }

    private PaymentEntity mapToExternal(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .rentEntity(rentRepository.getOne(payment.getRentId()))
                .paymentDeadline(payment.getPaymentDeadline())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .paid(payment.isPaid())
                .paymentType(payment.getPaymentType())
                .created(payment.getCreated())
                .build();
    }
}

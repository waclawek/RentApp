package pl.alpaka.rentalapp.domain.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void create(Payment payment);
    Optional<Payment> getPaymentById(int id);
    List<Payment> getAll();
    List<Payment> getPaymentsByRentId(int rentId);
    void update(Payment payment);
    void deleteById(int id);
    List<Payment> getPaymentsByOwner(String username);
    BigDecimal findBalanceByOwnerUsername(String username);
    BigDecimal findCurrentYearEarningsByOwnerUsername(String username);
    BigDecimal findSumOfDepositsByOwnerUsername(String username);
    List<Payment> getPaymentsByTenant(String username);
    BigDecimal findBalanceByTenantUsername(String username);

    BigDecimal findBalanceByRentId(int rentId);

    BigDecimal findDepositByRent(int rentId);
}

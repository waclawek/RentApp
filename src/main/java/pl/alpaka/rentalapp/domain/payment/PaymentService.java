package pl.alpaka.rentalapp.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    public void create(Payment payment){
        repository.create(payment);
    }

    public Payment getOneById(int paymentId){
        return repository.getPaymentById(paymentId).orElseThrow(()-> new IllegalArgumentException("No payment with this ID"));
    }

    public List<Payment> getAll(){
        return repository.getAll();
    }

    public void update(Payment payment){
        repository.update(payment);
    }

    public void deleteById(int paymentId){
        repository.deleteById(paymentId);
    }

    public List<Payment> getPaymentsByOwner(String username) {
        return repository.getPaymentsByOwner(username);
    }

    public List<Payment> getPaymentsByRent(int rentId) {
        return repository.getPaymentsByRentId(rentId);
    }

    public BigDecimal findBalanceByOwnerUsername(String username) { return repository.findBalanceByOwnerUsername(username);}

    public BigDecimal findCurrentYearEarningsByOwnerUsername(String username) {
        return repository.findCurrentYearEarningsByOwnerUsername(username);
    }

    public BigDecimal findSumOfDepositsByOwnerUsername(String username) {
        return  repository.findSumOfDepositsByOwnerUsername(username);
    }

    public List<Payment> getPaymentsByTenant(String username) {
        return repository.getPaymentsByTenant(username);
    }

    public BigDecimal findBalanceByTenantUsername(String username) {
        return repository.findBalanceByTenantUsername(username);
    }

    public BigDecimal findBalanceByRentId(int rentId) {
        return  repository.findBalanceByRentId(rentId);
    }

    public BigDecimal findDepositByRent(int rentId) {
        return  repository.findDepositByRent(rentId);
    }
}

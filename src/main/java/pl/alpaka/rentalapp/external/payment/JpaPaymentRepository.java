package pl.alpaka.rentalapp.external.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Integer> {

    @Query(value = "SELECT pe FROM PaymentEntity pe JOIN pe.rentEntity re JOIN re.apartmentEntity ae JOIN ae.owners ow WHERE ow.username = :username")
    List<PaymentEntity> getPaymentsByOwner(@Param("username") String username);

    @Query(value = "SELECT pe FROM PaymentEntity pe JOIN pe.rentEntity re JOIN re.tenants te WHERE te.username = :username")
    List<PaymentEntity> getPaymentsByTenant(@Param("username") String username);

    @Query(value = "SELECT pe FROM PaymentEntity pe JOIN pe.rentEntity re WHERE re.id = :rentId")
    List<PaymentEntity> getPaymentsByRentId(@Param("rentId") int rentId);

}

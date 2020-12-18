package pl.alpaka.rentalapp.external.rent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.util.List;

@Repository
public interface JpaRentRepository extends JpaRepository<RentEntity, Integer> {

    @Query(value = "SELECT re FROM RentEntity re JOIN re.apartmentEntity ae JOIN ae.owners ow WHERE ow.username = :username")
    List<RentEntity> getRentsByOwner(@Param("username") String username);

    @Query(value = "SELECT re FROM RentEntity re JOIN re.apartmentEntity ap WHERE ap.id = :apartmentId")
    List<RentEntity> getRentsByApartment(@Param("apartmentId") int apartmentId);

    @Query(value = "SELECT re FROM RentEntity re JOIN re.tenants tn WHERE tn.username = :username")
    List<RentEntity> getRentsByTenant(@Param("username") String username);
}


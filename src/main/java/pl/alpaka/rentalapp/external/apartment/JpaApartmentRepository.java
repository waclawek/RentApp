package pl.alpaka.rentalapp.external.apartment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.alpaka.rentalapp.domain.apartment.Apartment;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaApartmentRepository extends JpaRepository<ApartmentEntity, Integer> {

    @Query(value = "SELECT ap FROM ApartmentEntity ap JOIN ap.owners ow WHERE ow.username = :username")
    List<ApartmentEntity> getAllByOwnerUsername(@Param("username") String username);

    @Query(value = "SELECT ap FROM ApartmentEntity ap WHERE ap.name = :name")
    Optional<ApartmentEntity> findByName(@Param("name")String name);

    @Query(value = "SELECT ap FROM RentEntity re JOIN re.apartmentEntity ap JOIN re.tenants te WHERE te.username = :username")
    List<ApartmentEntity> getAllByTenantUsername(@Param("username") String username);
}

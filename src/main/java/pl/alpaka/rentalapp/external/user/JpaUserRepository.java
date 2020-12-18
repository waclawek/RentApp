package pl.alpaka.rentalapp.external.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.alpaka.rentalapp.domain.apartment.Apartment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaUserRepository  extends  JpaRepository<UserEntity, String>{
        Optional<UserEntity> findByUsername(String username);

        @Query(value = "SELECT ow FROM ApartmentEntity ap JOIN ap.owners ow WHERE ap.id=:apartment")
        Set<UserEntity> getUserEntityOwnersByApartmentId(@Param("apartment") Integer apartmentId);

        @Query(value = "SELECT tn FROM RentEntity rt JOIN rt.tenants tn WHERE rt.id=:rentId")
        List<UserEntity> getUserEntityTenantByRentId(@Param("rentId") Integer rentId);

        //todo zmianić te proste zapytania z HQl na queryMethod

        @Query(value = "SELECT ue FROM UserEntity ue WHERE ue.email=:email")
        Optional<UserEntity> findByEmail(@Param("email") String email);

        @Query(value = "SELECT ue FROM UserEntity ue WHERE ue.telephone=:telephone")
        Optional<UserEntity> findByTelephone(@Param("telephone") String telephone);

        @Query(value = "SELECT ue FROM UserEntity ue WHERE ue.firstname=:firstname")
        Optional<UserEntity> findByFirstname(@Param("firstname") String firstname);

        @Query(value = "SELECT ue FROM UserEntity ue WHERE ue.lastname=:lastname")
        Optional<UserEntity> findByLastname(@Param("lastname") String lastname);
}








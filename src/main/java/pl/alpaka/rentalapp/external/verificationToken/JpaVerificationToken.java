package pl.alpaka.rentalapp.external.verificationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaVerificationToken extends JpaRepository<VerificationTokenEntity, Integer> {

    @Query("SELECT vt FROM VerificationTokenEntity vt WHERE vt.token = :token")
    Optional<VerificationTokenEntity> findByToken(@Param("token") String token);
    @Query("SELECT vt FROM VerificationTokenEntity vt JOIN vt.userEntity ue WHERE ue.username = :username")
    Optional<VerificationTokenEntity> findByUserUsername(@Param("username") String username);

}

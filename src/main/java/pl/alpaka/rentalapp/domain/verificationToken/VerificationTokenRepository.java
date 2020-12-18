package pl.alpaka.rentalapp.domain.verificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    void create(VerificationToken verificationToken);
    void delete(VerificationToken verificationToken);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserUsername(String username);
}

package pl.alpaka.rentalapp.external.verificationToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationToken;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DatabaseVerificationTokenRepository implements VerificationTokenRepository {
    private final JpaVerificationToken verificationTokenRepository;
    private final JpaUserRepository userRepository;

    @Override
    public void create(VerificationToken verificationToken) {
        VerificationTokenEntity vte = VerificationTokenEntity.builder()
                .id(verificationToken.getId())
                .expiryDate(verificationToken.getExpiryDate())
                .token(verificationToken.getToken())
                .userEntity(userRepository.findByUsername(verificationToken.getUsername()).get())
                .build();
        verificationTokenRepository.save(vte);
    }

    @Override
    public void delete(VerificationToken verificationToken) {
        verificationTokenRepository.deleteById(verificationToken.getId());
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token).map(mapToDomain());
    }

    @Override
    public Optional<VerificationToken> findByUserUsername(String username) {
        return verificationTokenRepository.findByUserUsername(username).map(mapToDomain());
    }

    private Function<VerificationToken, VerificationTokenEntity> mapToExternal(){
        return verificationToken -> VerificationTokenEntity.builder()
                .id(verificationToken.getId())
                .token(verificationToken.getToken())
                .expiryDate(verificationToken.getExpiryDate())
                .userEntity(userRepository.findByUsername(verificationToken.getUsername()).get())
                .build();
    }

    private Function<VerificationTokenEntity, VerificationToken> mapToDomain(){
        return verificationTokenEntity -> VerificationToken.builder()
                .id(verificationTokenEntity.getId())
                .token(verificationTokenEntity.getToken())
                .expiryDate(verificationTokenEntity.getExpiryDate())
                .username(verificationTokenEntity.userEntity.getUsername())
                .build();
    }

}

package pl.alpaka.rentalapp.domain.verificationToken;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.alpaka.rentalapp.domain.email.EmailService;
import pl.alpaka.rentalapp.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository repository;
    private final EmailService emailService;
    private static final long EXPIRATIONTIME = 24 * 60;
    private static final long EXPIRATIONTIMEPASSWORDRESET = 30;

    public void createVerificationToken(User user) {
        //todo refactor to reduce number of tokens
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .username(user.getUsername())
                .expiryDate(calculateExpiry())
                .token(token)
                .build();
        repository.create(verificationToken);
        emailService.sendVerificationEmail(user, token);
    }

    private LocalDateTime calculateExpiry(){
        return LocalDateTime.now().plusMinutes(EXPIRATIONTIME);
    }

    private LocalDateTime calculateExpiryResetPassword(){
        return LocalDateTime.now().plusMinutes(EXPIRATIONTIMEPASSWORDRESET);
    }

    public void createResetPasswordToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .username(user.getUsername())
                .expiryDate(calculateExpiryResetPassword())
                .token(token)
                .build();
        repository.create(verificationToken);
        emailService.sendResetPasswordEmail(user, token);
    }
}

package pl.alpaka.rentalapp.external.verificationToken;

import lombok.*;
import pl.alpaka.rentalapp.external.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name="verification_token")
public class VerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "expiry_date")
    LocalDateTime expiryDate;
    String token;
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    UserEntity userEntity;
}

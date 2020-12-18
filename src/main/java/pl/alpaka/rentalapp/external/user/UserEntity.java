package pl.alpaka.rentalapp.external.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.alpaka.rentalapp.domain.user.User;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(unique = true)
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private Integer telephone;
    @Enumerated(EnumType.STRING)
    private User.userRole userRole;
    private boolean isEnabled;

    public void updateFromDomain(User user) {
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.telephone = user.getTelephone();
        this.userRole = getUserRole();
        this.isEnabled = user.isEnabled();
    }
}



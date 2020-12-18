package pl.alpaka.rentalapp.domain.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class User implements UserDetails {
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private Integer telephone;
    private userRole userRole;
    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //dodajemy prefix ROLE_ - jesli sprawdzamy czy uzytkownik ma role to bez prefixu
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole.toString());
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public enum userRole {OWNER, RENTIER}

    public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }
}



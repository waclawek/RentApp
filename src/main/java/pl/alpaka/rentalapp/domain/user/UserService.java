package pl.alpaka.rentalapp.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.alpaka.rentalapp.domain.email.EmailService;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenService;

import java.util.List;
import java.util.Optional;

@Setter
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void delete(String username) {
        userRepository.delete(username);
    }

    public void createOwner(User user) {
        if (user.getUsername().isEmpty()) throw new IllegalStateException("Username shouldn't be empty.");
        userRepository.getByUsername(user.getUsername())
                .ifPresent(usr ->
                {
                    throw new IllegalStateException("Couldn't use USERNAME " + user.getUsername());
                });
        user.encodePassword(passwordEncoder, user.getPassword());
        userRepository.create(user);
    }

    public void update(User user) {
           userRepository.update(user);
    }

    public void updateProfile(User user) {
        if (user.getPassword().isEmpty() || user.getPassword().isBlank()) {
            user.setPassword(userRepository.getByUsername(user.getUsername()).get().getPassword());
        } else {
            user.encodePassword(passwordEncoder, user.getPassword());
        }
        update(user);
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username" + username + " doesn't exists"));
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    public Optional<User> getByTelephone(String telephone) {
        return userRepository.getByTelephone(telephone);
    }

    public void registerOwner(UserDto userDto) throws IllegalArgumentException{
        if (userRepository.getByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with given email already exists");
        }

        User user = User.builder()
                .userRole(User.userRole.OWNER)
                .telephone(userDto.getTelephone())
                .email(userDto.getEmail())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .username(userDto.getEmail())
                .isEnabled(false)
                .build();

        user.encodePassword(passwordEncoder, userDto.getPassword());
        userRepository.create(user);

        verificationTokenService.createVerificationToken(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    public User createNewTenant(User newTenant) {
        User tenant = User.builder()
                .username(newTenant.getEmail())
                .firstname(newTenant.getFirstname())
                .lastname(newTenant.getLastname())
                .email(newTenant.getEmail())
                .telephone(newTenant.getTelephone())
                .userRole(User.userRole.RENTIER)
                .isEnabled(true)
                .build();
        //todo change to random
        //todo send welcome email to tenant
        tenant.encodePassword(passwordEncoder, newTenant.getLastname() + newTenant.getTelephone());
        userRepository.create(tenant);
        return tenant;
    }

    public void resetPasswordFirstStep(String email) {
        verificationTokenService.createResetPasswordToken(userRepository.getByEmail(email).get());
    }

    public void resetPasswordSecondStep(User user, String password){
        user.encodePassword(passwordEncoder, password);
        userRepository.update(user);
        emailService.sendResetPasswordSuccessEmail(user);
    }
}
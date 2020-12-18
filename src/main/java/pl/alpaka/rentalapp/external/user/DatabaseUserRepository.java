package pl.alpaka.rentalapp.external.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseUserRepository implements UserRepository {
    private final JpaUserRepository userRepository;

    @Override
    public void create(User user) {
        UserEntity entity = UserEntity.builder()
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .password(user.getPassword())
                .telephone(user.getTelephone())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
        userRepository.save(entity);
    }

    @Override
    public Set<User> getUsersOwnersFromApartment(Integer apartmentId) {
        return userRepository.getUserEntityOwnersByApartmentId(apartmentId).stream().map(mapToDomain2()).collect(Collectors.toSet());
    }

    @Override
    public List<User> getUserEntityTenantByRentId(Integer rentId) {
        return userRepository.getUserEntityTenantByRentId(rentId).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email).map(mapToDomain());
    }

    @Override
    public Optional<User> getByTelephone(String telephone) {
        return userRepository.findByTelephone(telephone).map(mapToDomain());
    }

    @Override
    public Optional<User> getByFirstname(String firstname) {
        return userRepository.findByFirstname(firstname).map(mapToDomain());
    }

    @Override
    public Optional<User> getByLastname(String lastname) {
        return userRepository.findByLastname(lastname).map(mapToDomain());
    }


    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username).map(mapToDomain());
    }

    public static Function<UserEntity, User> mapToDomain() {
        return ent -> User.builder()
                .username(ent.getUsername())
                .password(ent.getPassword())
                .isEnabled(ent.isEnabled())
                .userRole(ent.getUserRole())
                .email(ent.getEmail())
                .firstname(ent.getFirstname())
                .lastname(ent.getLastname())
                .telephone(ent.getTelephone())
                .build();
    }

    public static Function<User, UserEntity> mapToExternal() {
        return user -> UserEntity.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .telephone(user.getTelephone())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .isEnabled(user.isEnabled())
                .build();
    }

    @Override
    public void update(User user) {
        UserEntity entity = UserEntity.builder()
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .password(user.getPassword())
                .telephone(user.getTelephone())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .isEnabled(user.isEnabled())
                .build();
        userRepository.save(entity);
    }

    @Override
    public void delete(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll().stream().map(mapToDomain()).collect(Collectors.toList());
    }

    public static Function<UserEntity, User> mapToDomain2(){
        return userEntity -> User.builder()
                .username(userEntity.getUsername())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .password(userEntity.getPassword())
                .telephone(userEntity.getTelephone())
                .email(userEntity.getEmail())
                .userRole(userEntity.getUserRole())
                .build();
    }

}

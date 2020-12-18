package pl.alpaka.rentalapp.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    void create(User user);

    void update(User user);

    void delete(String username);

    List<User> getAll();

    Optional<User> getByUsername(String username);

    Set<User> getUsersOwnersFromApartment(Integer apartmentId);
    List<User> getUserEntityTenantByRentId(Integer rentId);
    //todo Tomasz: "To moim zdaniem trochę za dużo. Może spróbujcie zrobić metodę pododbną do tej,
    // jaką robiliśmy na zajęciach z Springa, która przyjmie obiekt z możliwymi parametrami do wyszukania i wyszuka po tych,
    // które są obecne"
    Optional<User> getByEmail(String email);
    Optional<User> getByTelephone(String telephone);
    Optional<User> getByFirstname(String firstname);
    Optional<User> getByLastname(String lastname);
}

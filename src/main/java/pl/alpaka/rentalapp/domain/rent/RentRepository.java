package pl.alpaka.rentalapp.domain.rent;

import pl.alpaka.rentalapp.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface RentRepository {
    void create(Rent rent);
    Optional<Rent> getRentById(int id);
    List<Rent> getAll();
    void update(Rent rent);
    void deleteById(int id);
    List<Rent> getRentsByApartment(int apartmentId);
    List<Rent> getRentsByOwner(String username);
    List<Rent> getRentsByTenant(String username);
}

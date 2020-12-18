package pl.alpaka.rentalapp.domain.apartment;

import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository {

    void create(Apartment apartment);

    Optional<Apartment> getById(int id);

    Optional<Apartment> getByName(String name);

    List<Apartment> getAll();

    void update(Apartment apartment);

    void deleteById(int id);

    List<Apartment> getAllByOwnerUsername(String username);

    List<Apartment> getAllByTenantUsername(String username);
}
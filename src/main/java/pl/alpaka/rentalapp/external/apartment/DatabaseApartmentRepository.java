package pl.alpaka.rentalapp.external.apartment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentRepository;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.user.DatabaseUserRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseApartmentRepository implements ApartmentRepository {

    private final JpaApartmentRepository apartmentRepository;
    private final JpaUserRepository userRepository;

    @Override
    public void create(Apartment apartment) {
        ApartmentEntity aE = ApartmentEntity.builder()
                .id(apartment.getId())
                .name(apartment.getName())
                .address(apartment.getAddress())
                .owners(getOwnerEntitySetFromApartment(apartment))
                .build();

        apartmentRepository.save(aE);
    }

    @Override
    public Optional<Apartment> getById(int id) {
       return apartmentRepository.findById(id).map(mapToDomain());
    }

    @Override
    public Optional<Apartment> getByName(String name) {
        return apartmentRepository.findByName(name).map(mapToDomain());
    }

    @Override
    public List<Apartment> getAll() {
        return apartmentRepository.findAll()
                .stream()
                .map(mapToDomain())
                .collect(Collectors.toList());
    }

    @Override
    public void update(Apartment apartment) {
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .id(apartment.getId())
                .name(apartment.getName())
                .address(apartment.getAddress())
                .owners(userRepository.getUserEntityOwnersByApartmentId(apartment.getId()))
                .build();
        apartmentRepository.save(apartmentEntity);
    }

    @Override
    public void deleteById(int id) {
        apartmentRepository.deleteById(id);

    }

    @Override
    public List<Apartment> getAllByOwnerUsername(String username) {
        return apartmentRepository.getAllByOwnerUsername(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public List<Apartment> getAllByTenantUsername(String username) {
        return apartmentRepository.getAllByTenantUsername(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    private Function<ApartmentEntity, Apartment> mapToDomain(){

        return apartmentEntity -> Apartment.builder()
                .id(apartmentEntity.getId())
                .address(apartmentEntity.getAddress())
                .name(apartmentEntity.getName())
                .owners(getOwnerSetFromApartmentEntity(apartmentEntity))
                .build();
    }

    private Set<User> getOwnerSetFromApartmentEntity(ApartmentEntity apartmentEntity){
       return apartmentEntity.getOwners().stream()
               .map(DatabaseUserRepository.mapToDomain())
               .collect(Collectors.toSet());
    }

    private Set<UserEntity> getOwnerEntitySetFromApartment(Apartment apartment){
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (User user : apartment.getOwners()) {
            userEntitySet.add(UserEntity.builder()
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .username(user.getUsername())
            .userRole(user.getUserRole())
            .telephone(user.getTelephone())
            .email(user.getEmail())
            .password(user.getPassword())
            .build());
        }
        return userEntitySet;
    }
}

package pl.alpaka.rentalapp.external.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.rent.RentRepository;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;
import pl.alpaka.rentalapp.external.payment.DatabasePaymentRepository;
import pl.alpaka.rentalapp.external.payment.PaymentEntity;
import pl.alpaka.rentalapp.external.user.DatabaseUserRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DatabaseRentRepository implements RentRepository {

    private final JpaRentRepository rentRepository;
    private final JpaApartmentRepository apartmentRepository;
    private final JpaUserRepository userRepository;

    @Override
    public void create(Rent rent) {
        RentEntity rE = RentEntity.builder()
                .id(rent.getId())
                .apartmentEntity(apartmentRepository.findById(rent.getApartmentId()).orElseThrow(()->new IllegalArgumentException("No apartment with this id")))
                .tenants(new LinkedList<>())
                .beginDate(rent.getBeginDate())
                .endDate(rent.getEndDate())
                .paymentList(new LinkedList<>())
                .name(rent.getName())
                .build();

        rentRepository.save(rE);
    }

    @Override
    public Optional<Rent> getRentById(int id) { return rentRepository.findById(id).map(mapToDomain());
    }

    @Override
    public List<Rent> getAll() {
        return rentRepository.findAll().stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public void update(Rent rent) {
        RentEntity rE = RentEntity.builder()
                .id(rent.getId())
                .apartmentEntity(apartmentRepository.findById(rent.getApartmentId()).orElseThrow(()->new IllegalArgumentException("No apartment with this id")))
                .beginDate(rent.getBeginDate())
                .tenants(userRepository.findAllById(rent.getTenants().stream().map(User::getUsername).collect(Collectors.toList())))
                .endDate(rent.getEndDate())
                .paymentList(getAllPaymentsEntityFromRentEntity(rent.getId()))
                .name(rent.getName())
                .build();

        rentRepository.save(rE);
    }

    @Override
    public void deleteById(int id) {
        rentRepository.deleteById(id);
    }

    @Override
    public List<Rent> getRentsByApartment(int apartmentId) {
        //todo change to PHQL
        return rentRepository.getRentsByApartment(apartmentId)
                .stream()
                .map(mapToDomain())
                .collect(Collectors.toList());
    }

    @Override
    public List<Rent> getRentsByOwner(String username) {
        //todo change to PHQL
        return rentRepository.getRentsByOwner(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public List<Rent> getRentsByTenant(String username) {
        return rentRepository.getRentsByTenant(username).stream().map(mapToDomain()).collect(Collectors.toList());
    }

    private List<UserEntity> getAllUserEntityFromRentEntity(int rentId){
        RentEntity rentEntity = rentRepository.findById(rentId).orElseThrow(()->new IllegalArgumentException("No rent with this Id"));
        return rentEntity.getTenants();
    }

    private List<PaymentEntity> getAllPaymentsEntityFromRentEntity(int rentId){
        RentEntity rentEntity = rentRepository.findById(rentId).orElseThrow(()->new IllegalArgumentException("No rent with this Id"));
        return rentEntity.getPaymentList();
    }

    private Function<RentEntity, Rent> mapToDomain(){
        return rentEntity -> Rent.builder()
                .id(rentEntity.getId())
                .apartmentId(rentEntity.getApartmentEntity().getId())
                .beginDate(rentEntity.getBeginDate())
                .endDate(rentEntity.getEndDate())
                .name(rentEntity.getName())
                //toDo change to PHQL
                .paymentList(rentEntity.getPaymentList().stream().map(DatabasePaymentRepository.mapToDomain()).collect(Collectors.toList()))
                .tenants(rentEntity.getTenants().stream().map(DatabaseUserRepository.mapToDomain2()).collect(Collectors.toList()))
                .build();
    }
}
package pl.alpaka.rentalapp.external.rent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.configuration.injection.MockInjection;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;
import pl.alpaka.rentalapp.external.apartment.DatabaseApartmentRepository;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;
import pl.alpaka.rentalapp.external.user.DatabaseUserRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DataBaseRentTest {

    private final JpaRentRepository rentRepository = Mockito.mock(JpaRentRepository.class);
    private final JpaApartmentRepository apartmentRepository = Mockito.mock(JpaApartmentRepository.class);
    private final JpaUserRepository userRepository = Mockito.mock(JpaUserRepository.class);
    private final DatabaseRentRepository dataBaseRentRepository = new DatabaseRentRepository(rentRepository, apartmentRepository, userRepository);
    private final DatabaseApartmentRepository dataBaseApartmentRepository = new DatabaseApartmentRepository(apartmentRepository, userRepository);
    private final DatabaseUserRepository databaseUserRepository = new DatabaseUserRepository(userRepository);
    ArgumentCaptor<RentEntity> argumentCaptor = ArgumentCaptor.forClass(RentEntity.class);

    @Test
    void shouldPersistNewRent(){
        //given
        Apartment apartment = Apartment.builder()
                .id(1)
                .address("Green street 15")
                .taskList(new LinkedList<>())
                .owners(new HashSet<>())
                .name("Green")
                .build();
        dataBaseApartmentRepository.create(apartment);
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .address(apartment.getAddress())
                .id(apartment.getId())
                .name(apartment.getName())
                .build();
        Rent rent = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(1))
                .endDate(LocalDate.now().plusWeeks(15))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        //when
        Mockito.when(apartmentRepository.findById(1)).thenReturn(Optional.of(apartmentEntity));
        dataBaseRentRepository.create(rent);
        Mockito.verify(rentRepository).save(argumentCaptor.capture());
        RentEntity value = argumentCaptor.getValue();
        //then
        Assertions.assertEquals(LocalDate.now().plusWeeks(1), value.getBeginDate());
        Assertions.assertEquals(LocalDate.now().plusWeeks(15), value.getEndDate());
        Assertions.assertEquals(1, value.getApartmentEntity().getId());
    }

    @Test
    void shouldUpdateRent(){
        //given
        Apartment apartment = Apartment.builder()
                .id(1)
                .address("Green street 15")
                .taskList(new LinkedList<>())
                .owners(new HashSet<>())
                .name("Green")
                .build();
        dataBaseApartmentRepository.create(apartment);
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .address(apartment.getAddress())
                .id(apartment.getId())
                .name(apartment.getName())
                .build();
        Rent rent = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(1))
                .endDate(LocalDate.now().plusWeeks(15))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        RentEntity rentEntity = RentEntity.builder()
                .apartmentEntity(apartmentEntity)
                .beginDate(rent.getBeginDate())
                .endDate(rent.getEndDate())
                .id(rent.getId())
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        //when
        Mockito.when(apartmentRepository.findById(1)).thenReturn(Optional.of(apartmentEntity));
        dataBaseRentRepository.create(rent);
        Mockito.when(rentRepository.findById(1)).thenReturn(Optional.of(rentEntity));
        Rent newRent = dataBaseRentRepository.getRentById(1).get();
        newRent.setBeginDate(LocalDate.now().plusWeeks(3));
        dataBaseRentRepository.update(newRent);
        Mockito.verify(rentRepository, Mockito.times(2)).save(argumentCaptor.capture());
        RentEntity newValue = argumentCaptor.getValue();
        //then
        Assertions.assertEquals(apartmentEntity, newValue.getApartmentEntity());
        Assertions.assertEquals(rent.getEndDate(), newValue.getEndDate());
        Assertions.assertNotEquals(rent.getBeginDate(), newValue.getBeginDate());
    }

}

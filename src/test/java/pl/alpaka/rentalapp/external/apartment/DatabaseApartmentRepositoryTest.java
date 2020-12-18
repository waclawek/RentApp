package pl.alpaka.rentalapp.external.apartment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;

public class DatabaseApartmentRepositoryTest {

    private final JpaApartmentRepository jpaApartmentRepository = Mockito.mock(JpaApartmentRepository.class);
    private final JpaUserRepository jpaUserRepository = Mockito.mock(JpaUserRepository.class);
    private final DatabaseApartmentRepository dataBaseApartmentRepository = new DatabaseApartmentRepository(jpaApartmentRepository, jpaUserRepository);
    private final ArgumentCaptor<ApartmentEntity> apartmentCaptor = ArgumentCaptor.forClass(ApartmentEntity.class);


    @Test
    void shouldPersistNewApartment() {
        //given
        Apartment apartment = Apartment.builder()
                .id(1)
                .name("Green")
                .address("Green 66")
                .taskList(new LinkedList<>())
                .owners(new HashSet<>())
                .build();
        User owner1 = User.builder()
                .firstname("John")
                .lastname("Johns")
                .email("jj@jmail.com")
                .password("12345")
                .telephone(1111)
                .username("jj@jmail.com")
                .userRole(User.userRole.OWNER)
                .build();
        apartment.getOwners().add(owner1);
        //when
        dataBaseApartmentRepository.create(apartment);
        Mockito.verify(jpaApartmentRepository).save(apartmentCaptor.capture());
        ApartmentEntity value = apartmentCaptor.getValue();
        //then
        Assertions.assertEquals("Green", value.getName());
        Assertions.assertEquals("Green 66", value.getAddress());
    }

    @Test
    void shouldGetApartmentById(){
        //given
        Integer id = 2;
        ApartmentEntity apartment = ApartmentEntity.builder()
                .id(id)
                .name("Orange")
                .address("Orange 63/6")
                .owners(new HashSet<>())
                .build();
        //when
        Mockito.when(jpaApartmentRepository.findById(id)).thenReturn(Optional.of(apartment));
        Optional<Apartment> apartment2 = dataBaseApartmentRepository.getById(id);
        //then
        Assertions.assertEquals("Orange", apartment2.get().getName());
        Assertions.assertEquals("Orange 63/6", apartment2.get().getAddress());
    }

    @Test
    void shouldGetApartmentByName(){
        //given
        String name = "Berliner";
        ApartmentEntity apartment = ApartmentEntity.builder()
                .id(3)
                .name(name)
                .address("B 7/6")
                .owners(new HashSet<>())
                .build();
        //when
        Mockito.when(jpaApartmentRepository.findByName(name)).thenReturn(Optional.of(apartment));
        Optional<Apartment> apartment1 = dataBaseApartmentRepository.getByName(name);
        //then
        Assertions.assertEquals(name, apartment1.get().getName());
        Assertions.assertEquals("B 7/6", apartment1.get().getAddress());
    }

    @Test
    void shouldUpdateApartment(){
        //given
        String name = "Parisian";
        ApartmentEntity apartment = ApartmentEntity.builder()
                .id(2)
                .name(name)
                .address("P 27/16")
                .owners(new HashSet<>())
                .build();
        //when
        Mockito.when(jpaApartmentRepository.findById(2)).thenReturn(Optional.of(apartment));
        Apartment apartment1 = dataBaseApartmentRepository.getById(2).get();
        apartment1.setName(name + "1");
        dataBaseApartmentRepository.update(apartment1);
        Mockito.verify(jpaApartmentRepository).save(apartmentCaptor.capture());
        ApartmentEntity value = apartmentCaptor.getValue();
        //then
        Assertions.assertEquals(apartment1.getName(), value.getName());
        Assertions.assertEquals(apartment1.getAddress(), value.getAddress());
    }
}

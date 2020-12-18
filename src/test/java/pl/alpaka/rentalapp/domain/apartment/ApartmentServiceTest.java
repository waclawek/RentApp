package pl.alpaka.rentalapp.domain.apartment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentServiceTest {
    private ApartmentRepository apartmentRepository = Mockito.mock(ApartmentRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private ApartmentService apartmentService = new ApartmentService(apartmentRepository, userRepository);


    @Test
    void shouldCreateNewApartment() {
        //given
        Apartment apartment = new Apartment(1, "Zielona", "Zielona 12/5", null, null);
        //when
        apartmentService.create(apartment);
        //then
        Mockito.verify(apartmentRepository).create(apartment);
    }

    @Test
    void shouldUpdateApartment() {
        //given
        Apartment apartment = new Apartment(44, "Niebieska", "Niebieska 1/25", null, null);
        //when
        apartmentService.create(apartment);
        apartment.setAddress("Niebieska 125");
        apartmentService.update(apartment);
        //then
        Mockito.verify(apartmentRepository).create(apartment);
        Mockito.verify(apartmentRepository).update(apartment);
    }

    @Test
    void shouldThrowExceptionIfIdExists(){
        //given
        Apartment apartment = new Apartment(6, "Biala", "Biala 15", null, null);
        Apartment apartment1 = new Apartment(6, "Zolta", "Zolta 55", null, null);
        //when
        apartmentService.create(apartment);
        Mockito.when(apartmentRepository.getById(6)).thenReturn(Optional.of(apartment));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> apartmentService.create(apartment1));
        //then
        assertEquals("Apartment with this ID: [6] already exists", ex.getMessage());
    }
}
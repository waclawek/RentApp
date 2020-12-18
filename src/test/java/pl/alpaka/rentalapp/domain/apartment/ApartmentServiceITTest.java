package pl.alpaka.rentalapp.domain.apartment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;
import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApartmentServiceITTest {

    @Autowired
    private JpaApartmentRepository apartmentRepository;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setup(){
        Apartment apartment1 = Apartment.builder()
                .id(1)
                .name("Green 15")
                .address("Green 15/64")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
                .build();
        Apartment apartment2 = Apartment.builder()
                .id(2)
                .name("Red 123")
                .address("Red 123/15")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
                .build();
        Apartment apartment3 = Apartment.builder()
                .id(3)
                .name("Blue 44")
                .address("Blue 44/6")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
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
        userService.createOwner(owner1);
        apartment1.getOwners().add(owner1);
        apartment3.getOwners().add(owner1);
        apartmentService.create(apartment1);
        apartmentService.create(apartment2);
        apartmentService.create(apartment3);
    }

    @Test
    void shouldCreateNewApartment() {
        //Given
        Apartment apartment = Apartment.builder()
                .id(4)
                .name("Black 15")
                .address("Black 15/64")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
                .build();
        //When
        apartmentService.create(apartment);
        Apartment apartment1 = apartmentService.findById(4);
        //Then
        Assertions.assertEquals("Black 15", apartment1.getName());
        Assertions.assertEquals("Black 15/64", apartment1.getAddress());
    }

    @Test
    void shouldFindAllApartments(){
        //when
        List<Apartment> apartmentList = apartmentService.getAll();
        //then
        Assertions.assertEquals(3, apartmentList.size());
    }

    @Test
    void shouldFindApartmentsByOwnerUsername(){
        //when
        List<Apartment> apartmentsOfOwner = apartmentService.getAllByOwnerUsername("jj@jmail.com");
        Apartment apartment1 = apartmentService.findById(1);
        Apartment apartment3 = apartmentService.findById(3);
        //then
        Assertions.assertEquals(2, apartmentsOfOwner.size());
        Assertions.assertTrue(apartmentsOfOwner.contains(apartment1));
        Assertions.assertTrue(apartmentsOfOwner.contains(apartment3));
    }

    @Test
    void shouldUpdateApartment(){
        //given
        Apartment apartment = apartmentService.findById(1);
        //when
        apartment.setAddress("Bernauer Straße 11");
        apartment.setName("Wall apartment");
        apartmentService.update(apartment);
        //then
        Assertions.assertTrue(apartmentService.findById(1).equals(apartment));
        Assertions.assertEquals("Bernauer Straße 11", apartmentService.findById(1).getAddress());
        Assertions.assertEquals("Wall apartment", apartmentService.findById(1).getName());
    }

    @Test
    void shouldDeleteApartment(){
        //when
        apartmentService.deleteById(2);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, ()-> apartmentService.findById(2));
        Optional<ApartmentEntity> apartmentOptional = apartmentRepository.findById(2);
        //then
        Assertions.assertEquals("no apartment with this id", ex.getMessage());
        Assertions.assertTrue(apartmentOptional.isEmpty());
    }

    @Test
    @WithMockUser(username="jj@jmail.com")
    void shouldCreateApartmentFromDataFromFrontend(){
        //given
        Apartment apartment = Apartment.builder()
                .id(4)
                .name("Yellow")
                .address("Yellow 111")
                .build();
        //when
        apartmentService.createNewApartment(apartment);
        Apartment createdApartment = apartmentService.findById(4);
        //then
        Assertions.assertEquals("Yellow", createdApartment.getName());
        Assertions.assertEquals("Yellow 111", createdApartment.getAddress());
    }
}
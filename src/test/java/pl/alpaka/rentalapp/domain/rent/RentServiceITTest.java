package pl.alpaka.rentalapp.domain.rent;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.email.EmailService;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;
import pl.alpaka.rentalapp.external.rent.JpaRentRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RentServiceITTest {

    @Autowired
    private JpaRentRepository rentRepository;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private JpaApartmentRepository apartmentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RentService rentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApartmentService apartmentService;

    @BeforeEach
    void setup(){
        User owner = User.builder()
                .firstname("John")
                .lastname("Johns")
                .email("jj@jmail.com")
                .password("12345")
                .telephone(1111)
                .username("johny")
                .userRole(User.userRole.OWNER)
                .build();
        User tenant = User.builder()
                .firstname("Tomy")
                .lastname("Thoms")
                .email("tt@tmail.com")
                .build();
        Apartment apartment = Apartment.builder()
                .id(1)
                .name("Green Apartment")
                .address("Green 145")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
                .build();
        Apartment apartment2 = Apartment.builder()
                .id(2)
                .name("Red Apartment")
                .address("Red 65")
                .owners(new HashSet<>())
                .taskList(new LinkedList<>())
                .build();
        userService.createOwner(owner);
        userService.createNewTenant(tenant);
        apartment.getOwners().add(owner);
        apartmentService.create(apartment);
        apartmentService.create(apartment2);
        Rent rent = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(3))
                .build();
        Rent rent2 = Rent.builder()
                .id(2)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(4))
                .endDate(LocalDate.now().plusWeeks(10))
                .build();
        Rent rent3 = Rent.builder()
                .id(3)
                .apartmentId(2)
                .beginDate(LocalDate.now().plusWeeks(2))
                .endDate(LocalDate.now().plusWeeks(7))
                .build();
        rentService.create(rent);
        rentService.create(rent2);
        rentService.create(rent3);
    }



    @Test
    void shouldCreateNewRent(){
        //Given
        Rent rent = Rent.builder()
                .id(4)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(11))
                .endDate(LocalDate.now().plusWeeks(19))
                .build();
        //When
        rentService.create(rent);
        Rent rent1 = rentService.getOneById(4);
        //Then
        Assertions.assertEquals(1, rent1.getApartmentId());
        Assertions.assertEquals(LocalDate.now().plusWeeks(11), rent1.getBeginDate());
        Assertions.assertEquals(LocalDate.now().plusWeeks(19), rent1.getEndDate());
    }

    @Test
    void shouldUpdateRent(){
        //given
        Rent rent = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(12))
                .endDate(LocalDate.now().plusWeeks(25))
                .build();
        //when
        rentService.create(rent);
        Rent rent1 = rentService.getOneById(1);
        rent1.getTenants().add(userService.getByUsername("tt@tmail.com"));
        rentService.update(rent1);
        Rent rent2 = rentService.getOneById(1);
        //then
        Assertions.assertTrue(rent2.getTenants().contains(userService.getByUsername("tt@tmail.com")));
    }

    @Test
    void shouldGetAllRents(){
        //when
        List<Rent> rents = rentService.getAll();
        //then
        Assertions.assertEquals(3, rents.size());
    }

    @Test
    void shouldReturnListOfRentsByApartmentOwner(){
        //when
        List<Rent> rents = rentService.getRentsByOwnerUsername("johny");
        //then
        Assertions.assertEquals(2, rents.size());
    }

    @Test
    void shouldAddTenantToRent(){
        //given
        User newTenant = User.builder()
                .firstname("Anna")
                .lastname("Smith")
                .email("as@gmail.com")
                .telephone(951357)
                .build();
        //when
        userService.createNewTenant(newTenant);
        rentService.addTenantToRent("as@gmail.com", 1);
        Rent rent = rentService.getOneById(1);
        //then
        Assertions.assertTrue(rent.getTenants().contains(userService.getByUsername("as@gmail.com")));
    }

}

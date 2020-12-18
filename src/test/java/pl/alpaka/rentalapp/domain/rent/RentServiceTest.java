package pl.alpaka.rentalapp.domain.rent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentRepository;
import pl.alpaka.rentalapp.domain.email.EmailService;
import pl.alpaka.rentalapp.domain.user.UserRepository;
import pl.alpaka.rentalapp.domain.user.UserService;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationToken;
import pl.alpaka.rentalapp.domain.verificationToken.VerificationTokenService;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RentServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RentRepository rentRepository = Mockito.mock(RentRepository.class);
    private VerificationTokenService verificationTokenService = Mockito.mock(VerificationTokenService.class);
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private EmailService emailService = Mockito.mock(EmailService.class);
    private UserService userService = new UserService(userRepository, passwordEncoder, verificationTokenService, emailService);
    private ApartmentRepository apartmentRepository = Mockito.mock(ApartmentRepository.class);
    private RentService rentService = new RentService(rentRepository,
                                                        userRepository,
                                                        userService,
                                                        apartmentRepository,
                                                        passwordEncoder);

/*
    @Test
    void shouldCreateRent(){
        //given
        Rent rent = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusWeeks(45))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        //when
        rentService.create(rent);
        //then
        Mockito.verify(rentRepository).create(rent);
    }

 */

    @Test
    void shouldThrowExceptionDueToWrongDates(){
        //given
        Rent rent = Rent.builder()
                .id(2)
                .apartmentId(2)
                .beginDate(LocalDate.now().plusDays(55))
                .endDate(LocalDate.now().plusWeeks(1))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        //when
        InvalidParameterException ex = Assertions.assertThrows(InvalidParameterException.class, ()-> rentService.create(rent));
        //then
        Assertions.assertEquals("Beginning date [" + rent.getBeginDate() + "] should be before end date [" + rent.getEndDate() + "]", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionDueToIntersectingRents(){
        //given
        Apartment apartment = new Apartment(1, "Blue", "Blue 1/25", new LinkedList<>(), new HashSet<>());
        Rent rent1 = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(5))
                .endDate(LocalDate.now().plusWeeks(15))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        Rent rent2 = Rent.builder()
                .id(2)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(10))
                .endDate(LocalDate.now().plusWeeks(45))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        //when
        apartmentRepository.create(apartment);
        List<Rent> rentList = new LinkedList<>();
        rentList.add(rent1);
        Mockito.when(rentService.getRentsByApartment(rent1.getApartmentId())).thenReturn(rentList);
        InvalidParameterException ex = Assertions.assertThrows(InvalidParameterException.class, ()-> rentService.create(rent2));
        //then
        Assertions.assertEquals("Rent beginning date is in already rented period. Rent finishes :[" + rent1.getEndDate() + "]", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionDueToIntersectingRents2(){
        //given
        Apartment apartment = new Apartment(1, "Blue", "Blue 1/25", new LinkedList<>(), new HashSet<>());
        Rent rent1 = Rent.builder()
                .id(1)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(5))
                .endDate(LocalDate.now().plusWeeks(15))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        Rent rent2 = Rent.builder()
                .id(2)
                .apartmentId(1)
                .beginDate(LocalDate.now().plusWeeks(1))
                .endDate(LocalDate.now().plusWeeks(8))
                .tenants(new LinkedList<>())
                .paymentList(new LinkedList<>())
                .build();
        //when
        apartmentRepository.create(apartment);
        List<Rent> rentList = new LinkedList<>();
        rentList.add(rent1);
        Mockito.when(rentService.getRentsByApartment(rent1.getApartmentId())).thenReturn(rentList);
        InvalidParameterException ex = Assertions.assertThrows(InvalidParameterException.class, ()-> rentService.create(rent2));
        //then
        Assertions.assertEquals("Rent end date is in already rented period. Rent starts:[" + rent1.getBeginDate() + "] finishes :[" + rent1.getEndDate() + "]", ex.getMessage());
    }
}

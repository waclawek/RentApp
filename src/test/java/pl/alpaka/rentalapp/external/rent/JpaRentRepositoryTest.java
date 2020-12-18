package pl.alpaka.rentalapp.external.rent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import pl.alpaka.rentalapp.domain.rent.Rent;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JpaRentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JpaRentRepository rentRepository;
    @Autowired
    private JpaApartmentRepository apartmentRepository;

    @Test
    void shouldPersistNewRentEntity(){
        //given
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .address("Green 55")
                .name("Green")
                .build();
        testEntityManager.persist(apartmentEntity);
        RentEntity rentEntity = RentEntity.builder()
                .apartmentEntity(apartmentEntity)
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(15))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        //when
        testEntityManager.persist(rentEntity);
        Optional<RentEntity> rentEntityOptional = rentRepository.findById(1);
        //then
        Assertions.assertTrue(rentEntityOptional.isPresent());
        Assertions.assertEquals(LocalDate.now(), rentEntityOptional.get().getBeginDate());
        Assertions.assertEquals(apartmentEntity, rentEntityOptional.get().getApartmentEntity());
    }

    @Test
    void shouldGetRentsByOwner(){
        //given
        UserEntity owner = UserEntity.builder()
                .firstname("Adam")
                .lastname("Adams")
                .email("aa@amail.com")
                .password("12345679")
                .telephone(1122334455)
                .username("aadams")
                .userRole(User.userRole.OWNER)
                .build();
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .address("Red 55")
                .name("Red")
                .owners(new HashSet<>())
                .build();
        apartmentEntity.getOwners().add(owner);
        ApartmentEntity apartmentEntity2 = ApartmentEntity.builder()
                .address("Red 55")
                .name("Red")
                .owners(new HashSet<>())
                .build();
        testEntityManager.persist(owner);
        testEntityManager.persist(apartmentEntity);
        testEntityManager.persist(apartmentEntity2);
        RentEntity rentEntity1 = RentEntity.builder()
                .apartmentEntity(apartmentEntity)
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(7))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        RentEntity rentEntity2 = RentEntity.builder()
                .apartmentEntity(apartmentEntity)
                .beginDate(LocalDate.now().plusWeeks(10))
                .endDate(LocalDate.now().plusWeeks(20))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        RentEntity rentEntity3 = RentEntity.builder()
                .apartmentEntity(apartmentEntity2)
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(15))
                .paymentList(new LinkedList<>())
                .tenants(new LinkedList<>())
                .build();
        //when
        testEntityManager.persist(rentEntity1);
        testEntityManager.persist(rentEntity2);
        testEntityManager.persist(rentEntity3);
        List<RentEntity> rentEntities = rentRepository.getRentsByOwner("aadams");
        //then
        Assertions.assertEquals(2, rentEntities.size());
        Assertions.assertTrue(rentEntities.contains(rentEntity1));
        Assertions.assertTrue(rentEntities.contains(rentEntity2));
        Assertions.assertFalse(rentEntities.contains(rentEntity3));
    }
}

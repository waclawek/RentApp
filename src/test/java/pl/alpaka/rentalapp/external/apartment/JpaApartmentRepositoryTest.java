package pl.alpaka.rentalapp.external.apartment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JpaApartmentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JpaApartmentRepository apartmentRepository;

    @Test
    void shouldGetApartmentByName(){
        //given
        ApartmentEntity apartmentEntity = ApartmentEntity.builder()
                .name("Red")
                .address("Red 15/5")
                .owners(new HashSet<>())
                .build();
        testEntityManager.persist(apartmentEntity);
        //when
        Optional<ApartmentEntity> result = apartmentRepository.findByName("Red");
        //then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Red 15/5", result.get().getAddress());
    }

    @Test
    void shouldGetApartmentsByOwnerName(){
        //given
        ApartmentEntity apartmentEntity1 = ApartmentEntity.builder()
                .name("First")
                .address("One 11/4")
                .owners(new HashSet<>())
                .build();
        ApartmentEntity apartmentEntity2 = ApartmentEntity.builder()
                .name("Second")
                .address("Two 22/5")
                .owners(new HashSet<>())
                .build();
        ApartmentEntity apartmentEntity3 = ApartmentEntity.builder()
                .name("Third")
                .address("Tree 33/6")
                .owners(new HashSet<>())
                .build();
        UserEntity owner = UserEntity.builder()
                .firstname("Jack")
                .lastname("Johns")
                .email("jackjohns@jmail.com")
                .password("5324")
                .telephone(3365)
                .username("jackjohns@jmail.com")
                .userRole(User.userRole.OWNER)
                .build();
        apartmentEntity1.getOwners().add(owner);
        apartmentEntity3.getOwners().add(owner);
        testEntityManager.persist(owner);
        testEntityManager.persist(apartmentEntity1);
        testEntityManager.persist(apartmentEntity2);
        testEntityManager.persist(apartmentEntity3);
        //when
        List<ApartmentEntity> list = apartmentRepository.getAllByOwnerUsername(owner.getUsername());
        //then
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(list.contains(apartmentEntity1));
        Assertions.assertTrue(list.contains(apartmentEntity3));
        Assertions.assertFalse(list.contains(apartmentEntity2));
    }
}

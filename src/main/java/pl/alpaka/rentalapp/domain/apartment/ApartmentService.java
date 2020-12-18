package pl.alpaka.rentalapp.domain.apartment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserRepository;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    public void create(Apartment apartment) {
        if (apartment.getId() != null) {
            apartmentRepository.getById(apartment.getId())
                    .ifPresent(ap -> {
                        throw new IllegalArgumentException("Apartment with this ID: [" + apartment.getId() + "] already exists");
                    });
        }
//        apartmentRepository.getByName(apartment.getName())
//                .ifPresent(ap -> {
//                    throw new IllegalArgumentException("Apartment with this name : [" + apartment.getName() + "] already exists");
//                });
        if(apartment.getName().isBlank()){
            apartment.setName(apartment.getAddress());
        }
        apartmentRepository.create(apartment);
    }

    public void createNewApartment(Apartment apartment) {
        apartment.setTaskList(new LinkedList<>());
        User user = userRepository.getByUsername(getCurrentUsername()).orElseThrow(() -> new IllegalArgumentException("user do not exists"));
        apartment.setOwners(new HashSet<>());
        apartment.getOwners().add(user);
        create(apartment);
    }

    public List<Apartment> getAll() {
        return apartmentRepository.getAll();
    }

    public List<Apartment> getAllByOwnerUsername(String username) {
        return apartmentRepository.getAllByOwnerUsername(username);
    }

    public Apartment findById(int id) {
        return apartmentRepository.getById(id).orElseThrow(() -> new IllegalArgumentException("no apartment with this id"));
    }

    public void update(Apartment apartment) {
        apartmentRepository.update(apartment);
    }

    public void deleteById(Integer apartmentId) {
        apartmentRepository.deleteById(apartmentId);
    }

    String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public List<Apartment> getAllByTenantUsername(String username) { return apartmentRepository.getAllByTenantUsername(username);
    }
}

package pl.alpaka.rentalapp.domain.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.alpaka.rentalapp.domain.apartment.ApartmentRepository;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserRepository;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public void addTenantToRent(String tenantUsername, Integer rentId) {
        Rent rentToAddTenant = getOneById(rentId);
        rentToAddTenant.getTenants().add(userRepository.getByUsername(tenantUsername)
                .orElseThrow(() -> new IllegalArgumentException("no user with " + tenantUsername + "username")));
        rentRepository.update(rentToAddTenant);
    }

    public void create(Rent rent) throws InvalidParameterException {
        if(rent.getBeginDate().isAfter(rent.getEndDate()) || rent.getBeginDate().isEqual(rent.getEndDate())){
            throw new InvalidParameterException("Beginning date [" + rent.getBeginDate() + "] should be before end date [" + rent.getEndDate() + "]");
        }
        List<Rent> rents = getRentsByApartment(rent.getApartmentId());
        for (Rent r : rents) {
            if(rent.getBeginDate().isAfter(r.getBeginDate()) && rent.getBeginDate().isBefore(r.getEndDate())){
                throw new InvalidParameterException("Rent beginning date is in already rented period. Rent finishes :[" + r.getEndDate() + "]");
            }
            if(rent.getEndDate().isAfter(r.getBeginDate()) && rent.getEndDate().isBefore(r.getEndDate())){
                throw new InvalidParameterException("Rent end date is in already rented period. Rent starts:[" + r.getBeginDate() + "] finishes :[" + r.getEndDate() + "]");
            }
        }
        rent.setTenants(new LinkedList<>());
        String rentName = createRentName(rent);
        rent.setName(rentName);
        rentRepository.create(rent);
    }

    private String createRentName(Rent rent) {
        StringBuilder sb = new StringBuilder();
        sb.append(apartmentRepository.getById(rent.getApartmentId()).get().getName());
        sb.append(" ");
        sb.append(rent.getBeginDate().getMonthValue());
        sb.append("/");
        sb.append(rent.getBeginDate().getYear());
        sb.append("-");
        sb.append(rent.getEndDate().getMonthValue());
        sb.append("/");
        sb.append(rent.getEndDate().getYear());
        return sb.toString();
    }

    public Rent getOneById(int rentId) {
        return rentRepository.getRentById(rentId).orElseThrow(() -> new IllegalArgumentException("No rent with this ID"));
    }

    public List<Rent> getAll() {
        return rentRepository.getAll();
    }

    public List<Rent> getRentsByApartment(int apartmentId) {
        return rentRepository.getRentsByApartment(apartmentId);
    }

    public List<Rent> getRentsByOwnerUsername(String username) {
        return rentRepository.getRentsByOwner(username);
    }

    public void update(Rent rent) {
        rentRepository.update(rent);
    }

    public void deleteById(int rentId) {
        rentRepository.deleteById(rentId);
    }

    public void deleteTenantFromRent(String username, Integer rentId) {
        Rent rent = rentRepository.getRentById(rentId).orElseThrow(() -> new IllegalArgumentException("no Rent with id = " + rentId));
        User tenant = userRepository.getByUsername(username).orElseThrow(() -> new IllegalArgumentException("no user with id = " + username));
        rent.getTenants().remove(tenant);
        rentRepository.update(rent);
    }

    public void checkNewTenant(User newTenant, Integer rentId) {
        Optional<User> emailUser = userRepository.getByEmail(newTenant.getEmail());
        if (emailUser.isPresent()) {
            if (emailUser.get().getTelephone().equals(newTenant.getTelephone())) {
                addTenantToRent(emailUser.get().getUsername(), rentId);
            }
        }
        if (emailUser.isEmpty()) {
            User newUser = userService.createNewTenant(newTenant);
            addTenantToRent(newUser.getUsername(), rentId);
        }
    }

    public void updateTenant(User newTenant, String username) {

        if (newTenant.getPassword() == null) {
            newTenant.setPassword(userService.getByUsername(username).getPassword());
        } else {
            newTenant.encodePassword(passwordEncoder, newTenant.getPassword());
        }
        newTenant.setUserRole(User.userRole.RENTIER);
        userService.update(newTenant);
    }

    public List<Rent> getRentsByTenantUsername(String username) {
        return rentRepository.getRentsByTenant(username);
    }
}

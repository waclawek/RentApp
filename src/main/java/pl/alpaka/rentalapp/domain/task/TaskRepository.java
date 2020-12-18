package pl.alpaka.rentalapp.domain.task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    void create(Task task);

    Optional<Task> getOneById(int id);

    List<Task> getAll();

    void update(Task task);

    void deleteById(int id);

    List<Task> getByUsername(String username);

    List<Task> getByApartmentId(int apartmentId);
}

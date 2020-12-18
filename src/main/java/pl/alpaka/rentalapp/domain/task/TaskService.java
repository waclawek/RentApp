package pl.alpaka.rentalapp.domain.task;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public void create(Task task) {
        taskRepository.create(task);
    }

    public List<Task> getAll() {
        return taskRepository.getAll();
    }

    public Task getOneById(int id) {
        return taskRepository.getOneById(id)
                .orElseThrow(() -> new IllegalArgumentException("task with such id doesn't exists"));
    }

    public void update(Task task) {
             taskRepository.update(task);
    }

    public void deleteById(int id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getByUsername(String username) {
        return taskRepository.getByUsername(username);
    }

    public List<Task> getAllByApartmentId(int apartmentId) {
        return taskRepository.getByApartmentId(apartmentId);
            }

}

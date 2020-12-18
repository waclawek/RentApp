package pl.alpaka.rentalapp.external.task;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.alpaka.rentalapp.domain.task.Status;
import pl.alpaka.rentalapp.domain.task.Task;
import pl.alpaka.rentalapp.domain.task.TaskRepository;
import pl.alpaka.rentalapp.external.apartment.JpaApartmentRepository;
import pl.alpaka.rentalapp.external.user.JpaUserRepository;
import pl.alpaka.rentalapp.external.user.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DataBaseTaskRepository implements TaskRepository {

    private final JpaTaskRepository taskRepository;
    private final JpaApartmentRepository apartmentRepository;
    private final JpaUserRepository userRepository;

    @Override
    public void create(Task task) {
        TaskEntity taskEntity = TaskEntity.builder()
                .id(task.getId())
                .apartmentEntity(apartmentRepository.getOne(task.getApartmentId()))
                .userEntity(userRepository.getOne(task.getUserId()))
                .creationDate(LocalDate.now())
                .description(task.getDescription())
                .status(Status.NEW)
                .build();
        taskRepository.save(taskEntity);
    }

    @Override
    public Optional<Task> getOneById(int id) {
        return taskRepository.findById(id).map(mapToDomain());
    }

    @Override
    public List<Task> getAll() {
        return taskRepository.findAll().stream().map(mapToDomain()).collect(Collectors.toList());
    }

    @Override
    public void update(Task task) {
        LocalDate ld = taskRepository.findById(task.getId()).get().getCreationDate();
        UserEntity us = taskRepository.findById(task.getId()).get().getUserEntity();
        TaskEntity taskEntity = TaskEntity.builder()
                .id(task.getId())
                .apartmentEntity(apartmentRepository.findById(task.getApartmentId()).orElseThrow(() -> new IllegalArgumentException("No apartment with this id")))
                .userEntity(us)
                .creationDate(ld)
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
        taskRepository.save(taskEntity);
    }

    @Override
    public void deleteById(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getByUsername(String username) {
        //todo refactor to PHQL
        return taskRepository.findAll().stream().filter(taskEntity -> taskEntity.getUserEntity().getUsername().equals(username))
                .map(mapToDomain())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getByApartmentId(int apartmentId) {
        return taskRepository.findAll().stream().filter(taskEntity -> taskEntity.getApartmentEntity().getId().equals(apartmentId))
                .map(mapToDomain())
                .collect(Collectors.toList());
    }

    private Function<TaskEntity, Task> mapToDomain() {
        return taskEntity -> Task.builder()
                .id(taskEntity.getId())
                .apartmentId(taskEntity.getApartmentEntity().getId())
                .userId(taskEntity.getUserEntity().getUsername())
                .creationDate(taskEntity.getCreationDate())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus())
                .build();
    }
}

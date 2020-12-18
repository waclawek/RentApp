package pl.alpaka.rentalapp.external.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, Integer> {
    Optional<TaskEntity> findById(int id);
}

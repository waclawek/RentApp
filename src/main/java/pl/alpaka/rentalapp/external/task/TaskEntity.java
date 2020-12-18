package pl.alpaka.rentalapp.external.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.alpaka.rentalapp.domain.task.Status;
import pl.alpaka.rentalapp.external.apartment.ApartmentEntity;
import pl.alpaka.rentalapp.external.user.UserEntity;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private ApartmentEntity apartmentEntity;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    private LocalDate creationDate;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

 }

package pl.alpaka.rentalapp.domain.apartment;

import lombok.*;
import pl.alpaka.rentalapp.domain.task.Task;
import pl.alpaka.rentalapp.domain.user.User;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Apartment {

    private Integer id;
    private String name;
    private String address;
    private List<Task> taskList;
    private Set<User> owners;
}
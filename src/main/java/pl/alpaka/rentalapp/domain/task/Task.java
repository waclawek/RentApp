package pl.alpaka.rentalapp.domain.task;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private Integer id;
    private Integer apartmentId;
    private String userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    private String description;
    private Status status;

}

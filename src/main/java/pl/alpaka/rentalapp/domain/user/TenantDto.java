package pl.alpaka.rentalapp.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {

    @NotEmpty(message = "Firstname can not be empty")
    private String firstname;
    @NotEmpty(message = "Lastname can not be empty")
    private String lastname;
    @NotEmpty(message = "Email can not be empty")
    @Email(message = "Provide proper e-mail address")
    private String email;
    private Integer telephone;

}

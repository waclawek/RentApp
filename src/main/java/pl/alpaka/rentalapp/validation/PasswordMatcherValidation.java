package pl.alpaka.rentalapp.validation;

import pl.alpaka.rentalapp.domain.user.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatcherValidation implements ConstraintValidator<PasswordMatcher, Object> {

    public void initialize(PasswordMatcher constraint) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserDto userDto = (UserDto) value;
        return userDto.getPassword().equals(userDto.getMatchingPassword());
    }

}

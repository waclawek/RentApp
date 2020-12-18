package pl.alpaka.rentalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import pl.alpaka.rentalapp.domain.user.User;

@SpringBootApplication
//@PropertySource(value = {"application-ext.properties", "application.properties"}, ignoreResourceNotFound = true)
public class RentalappApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalappApplication.class, args);
    }

}

package train.booking.train.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Train Booking System")
                        .version("1.0")
                        .description("The Train Booking System API provides a comprehensive" +
                                " set of endpoints for managing train bookings efficiently. " +
                                "Users can search for trains, book tickets, " +
                                "manage their reservations, and access real-time updates " +
                                "on train schedules. This API is designed to facilitate " +
                                "seamless interactions between users and the train booking platform," +
                                " ensuring a smooth travel experience."));

    }
}
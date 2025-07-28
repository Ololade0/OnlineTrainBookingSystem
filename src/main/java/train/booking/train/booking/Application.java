package train.booking.train.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJms
@EnableScheduling
//@PropertySource(value = {"file:/data/TrainBooking/application.properties"})
public class Application {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	}



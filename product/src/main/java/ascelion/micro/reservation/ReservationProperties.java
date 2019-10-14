package ascelion.micro.reservation;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reservation")
@Getter
@Setter
public class ReservationProperties {

	private Duration availability = Duration.of(1, ChronoUnit.DAYS);
	private Duration checkInterval = Duration.of(1, ChronoUnit.HOURS);
}

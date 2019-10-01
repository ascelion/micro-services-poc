package ascelion.micro.reservations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationQueueConfig {

	@Bean
	public Queue reservationQueue() {
		return new Queue(ReservationExpired.QUEUE_NAME);
	}
}

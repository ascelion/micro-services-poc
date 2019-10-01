package ascelion.micro.reservations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationExchangeConfig {

	@Autowired
	private Queue reservationQueue;

	@Bean
	public FanoutExchange reservationExchange() {
		return new FanoutExchange(ReservationExpired.QUEUE_NAME);
	}

	@Bean
	public Binding reservationBinding() {
		return BindingBuilder.bind(this.reservationQueue)
				.to(reservationExchange());
	}
}

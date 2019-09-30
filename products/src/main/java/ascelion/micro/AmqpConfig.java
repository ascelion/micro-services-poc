package ascelion.micro;

import ascelion.micro.reservations.ReservationExpired;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ConditionalOnClass(RabbitAutoConfiguration.class)
public class AmqpConfig {

	@Autowired
	private ConnectionFactory cf;

	@Bean
	public TopicExchange reservationExchange() {
		return new TopicExchange(ReservationExpired.QUEUE_NAME);
	}

	@Bean
	public Queue reservationQueue() {
		return new Queue(ReservationExpired.QUEUE_NAME);
	}

	@Bean
	public Binding reservationBinding() {
		return BindingBuilder.bind(reservationQueue())
				.to(reservationExchange())
				.with(ReservationExpired.QUEUE_NAME);
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(this.cf);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}

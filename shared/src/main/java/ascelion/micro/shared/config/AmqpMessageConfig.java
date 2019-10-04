package ascelion.micro.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ConditionalOnClass(MessageConverter.class)
public class AmqpMessageConfig {
	@Bean("amqpMessageConverter")
	public MessageConverter messageConverter(ObjectMapper om) {
		return new Jackson2JsonMessageConverter(om);
	}
}

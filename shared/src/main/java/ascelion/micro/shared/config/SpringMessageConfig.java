package ascelion.micro.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
@ConditionalOnClass(MessageConverter.class)
public class SpringMessageConfig {
	@Bean("springMessageConverter")
	public MessageConverter messageConverter(ObjectMapper om) {
		final var mc = new MappingJackson2MessageConverter();

		mc.setObjectMapper(om);

		return mc;
	}
}

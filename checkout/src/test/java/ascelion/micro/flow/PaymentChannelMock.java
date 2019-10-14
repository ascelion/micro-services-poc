package ascelion.micro.flow;

import ascelion.micro.payment.api.PaymentChannel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@TestConfiguration
public class PaymentChannelMock {
	@MockBean(name = PaymentChannel.OUTPUT)
	private MessageChannel output;
	@MockBean(name = PaymentChannel.INPUT)
	private SubscribableChannel input;

	@Bean
	@Primary
	public PaymentChannel paymentChannel() {
		final PaymentChannel channel = mock(PaymentChannel.class);

		when(channel.output())
				.thenReturn(this.output);
		when(channel.input())
				.thenReturn(this.input);

		return channel;
	}
}

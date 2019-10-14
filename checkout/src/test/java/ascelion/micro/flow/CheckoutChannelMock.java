package ascelion.micro.flow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import ascelion.micro.checkout.api.CheckoutChannel;
import ascelion.micro.shared.message.MessagePayload;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@TestConfiguration
public class CheckoutChannelMock {
	@MockBean(name = CheckoutChannel.OUTPUT, answer = Answers.CALLS_REAL_METHODS)
	private MessageChannel output;
	@MockBean(name = CheckoutChannel.INPUT)
	private SubscribableChannel input;
	private final ScheduledExecutorService schedService = Executors.newSingleThreadScheduledExecutor();

	@Bean
	@Primary
	public CheckoutChannel checkoutChannel() {
		final CheckoutChannel channel = mock(CheckoutChannel.class);

		when(channel.output())
				.thenReturn(this.output);
		when(channel.input())
				.thenReturn(this.input);

		return channel;
	}

	public <T> Message<MessagePayload<T>> outputMessage() {
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Message<MessagePayload<T>>> cap = ArgumentCaptor.forClass(Message.class);

		verify(this.output, times(1))
				.send(cap.capture(), anyLong());

		return cap.getValue();
	}

	public <T> CheckoutChannelMock outputAction(Consumer<Message<MessagePayload<T>>> action) {
		final Answer<?> answer = ivc -> {
			this.schedService.schedule(() -> action.accept(ivc.getArgument(0)), 100, TimeUnit.MILLISECONDS);

			return null;
		};

		reset(this.output);

		doAnswer(answer)
				.when(this.output)
				.send(any(Message.class), anyLong());

		return this;
	}
}

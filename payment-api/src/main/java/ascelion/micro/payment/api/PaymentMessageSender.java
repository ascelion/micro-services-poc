package ascelion.micro.payment.api;

import ascelion.micro.shared.message.MessageSenderAdapter;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(PaymentChannel.class)
public class PaymentMessageSender<T> extends MessageSenderAdapter<T> {
	public PaymentMessageSender(PaymentChannel channel) {
		super(channel.output());
	}
}

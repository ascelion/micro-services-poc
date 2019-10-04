package ascelion.micro.checkout.api;

import ascelion.micro.shared.message.MessageSenderAdapter;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CheckoutChannel.class)
public class CheckoutMessageSender<T> extends MessageSenderAdapter<T> {
	public CheckoutMessageSender(CheckoutChannel channel) {
		super(channel.output());
	}
}

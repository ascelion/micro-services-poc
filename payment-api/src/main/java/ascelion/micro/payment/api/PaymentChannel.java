package ascelion.micro.payment.api;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface PaymentChannel {
	static public final String PAYMENT_MESSAGE = "PAYMENT";

	String INPUT = "payment-input";
	String OUTPUT = "payment-output";

	@Input(INPUT)
	SubscribableChannel input();

	@Output(OUTPUT)
	MessageChannel output();
}

package ascelion.micro.checkout.api;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CheckoutChannel {
	String BASKET_MESSAGE = "BASKET";
	String CUSTOMER_MESSAGE = "CUSTOMER";
	String SHIPPING_MESSAGE = "SHIPPING";

	String INPUT = "checkout-input";
	String OUTPUT = "checkout-output";

	@Input(INPUT)
	SubscribableChannel input();

	@Output(OUTPUT)
	MessageChannel output();
}

package ascelion.micro.customer;

import java.util.Optional;
import java.util.UUID;

import ascelion.micro.checkout.api.CheckoutChannel;
import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.customer.api.Customer;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.CUSTOMER_MESSAGE;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CheckoutChannel.class)
public class CustomerRequestListener {

	@Autowired
	private CheckoutMessageSender<Customer> cms;
	@Autowired
	private CustomerRepo repo;

	@StreamListener(target = CheckoutChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + CUSTOMER_MESSAGE + "_REQUEST'")
	public void messageReceived(@Payload MessagePayload<UUID> payload, @Header(HEADER_CORRELATION) UUID pid) {
		final UUID customerId = payload.get();
		final Optional<Customer> customer = this.repo.findById(customerId);

		this.cms.send(Direction.RESPONSE, pid, CUSTOMER_MESSAGE, MessagePayload.of(customer));
	}
}

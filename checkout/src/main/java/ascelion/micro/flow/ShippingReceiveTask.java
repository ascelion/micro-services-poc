package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.checkout.api.CheckoutChannel;
import ascelion.micro.shared.message.MessagePayload;

import static ascelion.micro.checkout.api.CheckoutChannel.SHIPPING_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.SHIPPING_RECEIVE_TASK;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service(SHIPPING_RECEIVE_TASK)
@EnableBinding(CheckoutChannel.class)
public class ShippingReceiveTask extends AbstractReceiveTask<String> {
	@Override
	@StreamListener(target = CheckoutChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + SHIPPING_MESSAGE + "_RESPONSE'")
	public void messageReceived(@Payload MessagePayload<String> payload, @Header(HEADER_CORRELATION) UUID pid, @Header(HEADER_KIND) String kind) {
		received(payload, pid, kind);
	}
}

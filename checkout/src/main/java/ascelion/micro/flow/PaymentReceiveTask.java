package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.payment.api.PaymentChannel;
import ascelion.micro.shared.message.MessagePayload;

import static ascelion.micro.flow.CheckoutConstants.PAYMENT_RECEIVE_TASK;
import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service(PAYMENT_RECEIVE_TASK)
@EnableBinding(PaymentChannel.class)
@RequiredArgsConstructor
public class PaymentReceiveTask extends AbstractReceiveTask<UUID> {

	@Override
	@StreamListener(target = PaymentChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + PAYMENT_MESSAGE + "_RESPONSE'")
	public void messageReceived(@Payload MessagePayload<UUID> payload, @Header(HEADER_CORRELATION) UUID id, @Header(HEADER_KIND) String kind) {
		received(payload, id, kind);
	}
}

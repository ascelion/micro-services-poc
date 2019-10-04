package ascelion.micro.payment;

import java.util.UUID;

import ascelion.micro.card.Card;
import ascelion.micro.card.CardRepo;
import ascelion.micro.payment.api.PaymentRequest;
import ascelion.micro.payment.api.PaymentChannel;
import ascelion.micro.shared.message.MessagePayload;

import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(PaymentChannel.class)
public class PaymentRequestListener {
	@Autowired
	private CardRepo cards;
	@Autowired
	private PaymentRepo payments;

	@StreamListener(target = PaymentChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + PAYMENT_MESSAGE + "_REQUEST'")
	public void messageReceived(@Payload MessagePayload<PaymentRequest> payload, @Header(HEADER_CORRELATION) UUID pid) {
		final PaymentRequest request = payload.get();
		final Card card = this.cards.getByNumber(request.card);
		final Payment payment = new Payment(card, request.amount, pid);

		this.payments.save(payment);
	}
}

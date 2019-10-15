package ascelion.micro.payment;

import java.util.UUID;

import ascelion.micro.card.CardRepo;
import ascelion.micro.payment.api.PaymentChannel;
import ascelion.micro.payment.api.PaymentRequest;
import ascelion.micro.shared.message.MessagePayload;

import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static ascelion.micro.payment.api.PaymentChannel.REFUND_MESSAGE;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;

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
	public void paymentRequested(@Payload MessagePayload<PaymentRequest> payload, @Header(HEADER_CORRELATION) UUID cid) {
		final var request = payload.get();
		final var card = this.cards.getByNumber(request.card);
		final var payment = new Payment(card, request.amount, cid);

		this.payments.save(payment);
	}

	@StreamListener(target = PaymentChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + REFUND_MESSAGE + "_REQUEST'")
	public void refundRequested(@Payload MessagePayload<UUID> payload, @Header(HEADER_CORRELATION) UUID cid) {
		final var id = payload.get();
		final var payment = this.payments.getById(id);

		payment.refund();
		payment.getCard().getAccount().credit(payment.getAmount());

		this.payments.save(payment);
	}
}

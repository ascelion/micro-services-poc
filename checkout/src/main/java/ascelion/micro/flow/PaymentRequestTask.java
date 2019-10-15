package ascelion.micro.flow;

import java.math.BigDecimal;

import ascelion.micro.customer.api.Customer;
import ascelion.micro.payment.api.PaymentMessageSender;
import ascelion.micro.payment.api.PaymentRequest;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_VAR;
import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import org.slf4j.Logger;

@Action(PAYMENT_REQUEST_TASK)
public class PaymentRequestTask extends AbstractSendTask<PaymentRequest> {
	static private final Logger LOG = loggerForThisClass();

	public PaymentRequestTask(PaymentMessageSender<PaymentRequest> pms) {
		super(pms);
	}

	@Override
	protected void execute() {
		final Customer customer = getVariable(CUSTOMER_RESPONSE_VAR);
		final ReservationResponse[] reservations = getVariable(RESERVATIONS_VAR);
		var amount = BigDecimal.ZERO;

		for (final var rsp : reservations) {
			amount = amount.add(rsp.getQuantity().multiply(rsp.getPrice()));
		}

		final var card = customer.getCards().iterator().next().getValue();
		final var request = new PaymentRequest(card, amount);

		LOG.info("Basket[{}]: requesting payment: {}", basketId(), amount);

		this.msa.send(Direction.REQUEST, basketId(), PAYMENT_MESSAGE, MessagePayload.of(request));
	}
}

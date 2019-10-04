package ascelion.micro.flow;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import ascelion.micro.customer.api.Customer;
import ascelion.micro.payment.api.PaymentMessageSender;
import ascelion.micro.payment.api.PaymentRequest;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.*;
import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;

import org.springframework.stereotype.Service;

@Service(PAYMENT_REQUEST_TASK)
public class PaymentRequestTask extends AbstractSendTask<PaymentRequest> {

	public PaymentRequestTask(PaymentMessageSender<PaymentRequest> pms) {
		super(pms);
	}

	@Override
	protected void execute(UUID pid) {
		final Customer customer = getVariable(CUSTOMER_RESPONSE_VAR);
		final ReservationResponse[] reservations = getVariable(RESERVATIONS_VAR);
		BigDecimal amount = BigDecimal.ZERO;

		for (final ReservationResponse rsp : reservations) {
			amount = amount.add(rsp.getQuantity().multiply(rsp.getPrice()));
		}

		final String card = customer.getCards().iterator().next().getValue();
		final PaymentRequest request = new PaymentRequest(card, amount);

		this.msa.send(Direction.REQUEST, pid, PAYMENT_MESSAGE, MessagePayload.of(request));
	}
}

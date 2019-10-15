package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.payment.api.PaymentMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REFUND_TASK;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_RESPONSE_VAR;
import static ascelion.micro.payment.api.PaymentChannel.REFUND_MESSAGE;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import org.slf4j.Logger;

@Action(PAYMENT_REFUND_TASK)
public class PaymentRefundTask extends AbstractSendTask<UUID> {

	static private final Logger LOG = loggerForThisClass();

	public PaymentRefundTask(PaymentMessageSender<UUID> msa) {
		super(msa);
	}

	@Override
	protected void execute() {
		final UUID paymentId = getVariable(PAYMENT_RESPONSE_VAR);

		LOG.info("Basket[{}]: refund: {}", basketId(), paymentId);

		this.msa.send(Direction.REQUEST, basketId(), REFUND_MESSAGE, MessagePayload.of(paymentId));
	}
}

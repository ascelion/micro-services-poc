package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.CUSTOMER_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_REQUEST_TASK;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import org.slf4j.Logger;

@Action(CUSTOMER_REQUEST_TASK)
public class CustomerRequestTask extends AbstractSendTask<UUID> {
	static private final Logger LOG = loggerForThisClass();

	public CustomerRequestTask(CheckoutMessageSender<UUID> cms) {
		super(cms);
	}

	@Override
	protected void execute() {
		final Basket basket = getVariable(BASKET_RESPONSE_VAR);

		LOG.info("Basket[{}]: getting customer {}", basket.getId(), basket.getCustomerId());

		this.msa.send(Direction.REQUEST, basket.getId(), CUSTOMER_MESSAGE, MessagePayload.of(basket.getCustomerId()));
	}
}

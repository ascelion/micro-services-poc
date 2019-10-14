package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.CUSTOMER_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_REQUEST_TASK;

import org.springframework.stereotype.Service;

@Service(CUSTOMER_REQUEST_TASK)
public class CustomerRequestTask extends AbstractSendTask<UUID> {
	public CustomerRequestTask(CheckoutMessageSender<UUID> cms) {
		super(cms);
	}

	@Override
	protected void execute() {
		final Basket basket = getVariable(BASKET_RESPONSE_VAR);

		this.msa.send(Direction.REQUEST, basketId(), CUSTOMER_MESSAGE, MessagePayload.of(basket.getCustomerId()));
	}
}

package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_ID_VAR;
import static ascelion.micro.flow.CheckoutConstants.BASKET_REQUEST_TASK;

import org.springframework.stereotype.Service;

@Service(BASKET_REQUEST_TASK)
public class BasketRequestTask extends AbstractSendTask<UUID> {
	public BasketRequestTask(CheckoutMessageSender<UUID> cms) {
		super(cms);
	}

	@Override
	protected void execute(UUID pid) {
		final UUID basketId = getVariable(BASKET_ID_VAR);

		this.msa.send(Direction.REQUEST, pid, BASKET_MESSAGE, MessagePayload.of(basketId));
	}
}

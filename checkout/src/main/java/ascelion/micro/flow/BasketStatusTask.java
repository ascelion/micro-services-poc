package ascelion.micro.flow;

import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_STATUS_TASK;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import lombok.Setter;
import org.camunda.bpm.engine.delegate.Expression;
import org.springframework.stereotype.Service;

@Service(BASKET_STATUS_TASK)
public class BasketStatusTask extends AbstractSendTask<String> {

	@Setter
	private Expression status;

	public BasketStatusTask(CheckoutMessageSender<String> cms) {
		super(cms);
	}

	@Override
	protected void execute() {
		final var value = trimToNull(evaluate(this.status));

		this.msa.send(Direction.REQUEST, basketId(), BASKET_MESSAGE, MessagePayload.of(value));
	}
}

package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.shared.message.MessagePayload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractReceiveTask<T> {

	@Autowired
	private RuntimeService camunda;

	public abstract void messageReceived(MessagePayload<T> payload, UUID id, String kind);

	protected final void received(MessagePayload<T> payload, UUID basketId, String kind) {
		final var name = CaseUtils.toCamelCase(kind, false, '_');

		log.info("Basket[{}]: received {}", basketId, payload);

		this.camunda
				.createMessageCorrelation(kind)
				.processInstanceBusinessKey(basketId.toString())
				.setVariable(name, payload.orElse(null))
				.correlate();
	}

}

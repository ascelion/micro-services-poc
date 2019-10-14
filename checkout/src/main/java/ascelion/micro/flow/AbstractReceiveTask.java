package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.shared.message.MessagePayload;

import org.apache.commons.text.CaseUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractReceiveTask<T> {
	@Autowired
	private RuntimeService camunda;

	public abstract void messageReceived(MessagePayload<T> payload, UUID id, String kind);

	protected final void received(MessagePayload<T> payload, UUID basketId, String kind) {
		final var name = CaseUtils.toCamelCase(kind, false, '_');

		this.camunda
				.createMessageCorrelation(kind)
				.processInstanceBusinessKey(basketId.toString())
				.setVariable(name, payload.orElse(null))
				.correlate();
	}

}

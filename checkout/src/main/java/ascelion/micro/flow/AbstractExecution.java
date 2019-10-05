package ascelion.micro.flow;

import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

public abstract class AbstractExecution {

	static private final ThreadLocal<Map<String, Object>> VARIABLES = new ThreadLocal<>();

	@SuppressWarnings("unchecked")
	protected final <T> T getVariable(String name) {
		final var variables = VARIABLES.get();
		final var value = variables.get(name);

		if (value == null) {
			throw new BpmnError("VARIABLE_NOT_FOUND", "Variable not found: " + name);
		}

		return (T) value;
	}

	protected void setVariable(String name, Object value) {
		VARIABLES.get().put(name, value);
	}

	protected void doExecute(DelegateExecution execution) throws Exception {
		VARIABLES.set(execution.getVariables());

		try {
			execute(UUID.fromString(execution.getProcessInstanceId()));

			execution.setVariables(VARIABLES.get());
		} finally {
			VARIABLES.remove();
		}
	}

	protected abstract void execute(UUID pid);
}

package ascelion.micro.flow;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public abstract class AbstractExecution implements JavaDelegate, ExecutionListener {

	static private final ThreadLocal<Map<String, Object>> VARIABLES = new ThreadLocal<>();
	static private final ThreadLocal<DelegateExecution> EXECUTION = new ThreadLocal<>();

	@Override
	public final void execute(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getVariable(String name) {
		final var variables = VARIABLES.get();
		final var value = variables.get(name);

		if (value == null) {
			throw new BpmnError("VARIABLE_NOT_FOUND", "Variable not found: " + name);
		}

		return (T) value;
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getVariableOr(String name, Supplier<T> defValue) {
		final var variables = VARIABLES.get();
		final var value = variables.get(name);

		if (value == null) {
			return defValue != null ? defValue.get() : null;
		}

		return (T) value;
	}

	protected void setVariable(String name, Object value) {
		VARIABLES.get().put(name, value);
	}

	protected void doExecute(DelegateExecution execution) throws Exception {
		EXECUTION.set(execution);
		VARIABLES.set(execution.getVariables());

		try {
			execute();

			execution.setVariables(VARIABLES.get());
		} finally {
			VARIABLES.remove();
			EXECUTION.remove();
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T evaluate(Expression expression) {
		return expression != null ? (T) expression.getValue(EXECUTION.get()) : null;
	}

	protected final UUID basketId() {
		return UUID.fromString(EXECUTION.get().getBusinessKey());
	}

	protected abstract void execute();
}

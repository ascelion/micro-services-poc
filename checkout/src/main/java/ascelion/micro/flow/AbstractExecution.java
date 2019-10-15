package ascelion.micro.flow;

import java.util.UUID;
import java.util.function.Supplier;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public abstract class AbstractExecution implements JavaDelegate, ExecutionListener {

	private DelegateExecution execution;

	@Override
	public final void execute(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}

	@Override
	public final void notify(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}

	protected final void doExecute(DelegateExecution execution) throws Exception {
		this.execution = execution;

		execute();
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getVariable(String name) {
		final var value = this.execution.getVariable(name);

		if (value == null) {
			throw new BpmnError("VARIABLE_NOT_FOUND", "Variable not found: " + name);
		}

		return (T) value;
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getVariableOr(String name, Supplier<T> defValue) {
		final var value = this.execution.getVariable(name);

		if (value == null) {
			return defValue != null ? defValue.get() : null;
		}

		return (T) value;
	}

	protected final void setVariable(String name, Object value) {
		this.execution.setVariable(name, value);
	}

	@SuppressWarnings("unchecked")
	protected final <T> T evaluate(Expression expression) {
		return expression != null ? (T) expression.getValue(this.execution) : null;
	}

	protected final UUID basketId() {
		return UUID.fromString(this.execution.getBusinessKey());
	}

	protected abstract void execute();
}

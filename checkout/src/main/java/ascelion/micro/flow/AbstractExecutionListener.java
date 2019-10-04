package ascelion.micro.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public abstract class AbstractExecutionListener extends AbstractExecution implements ExecutionListener {
	@Override
	public final void notify(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}
}

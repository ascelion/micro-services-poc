package ascelion.micro.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public abstract class AbstractTask extends AbstractExecution implements JavaDelegate {
	@Override
	public final void execute(DelegateExecution execution) throws Exception {
		doExecute(execution);
	}
}

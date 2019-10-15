package ascelion.micro.flow;

import static ascelion.micro.flow.CheckoutConstants.VERIFY_VARIABLE_LISTENER;

import lombok.Setter;
import org.camunda.bpm.engine.delegate.Expression;
import org.springframework.context.annotation.Scope;

@Action(VERIFY_VARIABLE_LISTENER)
@Scope("prototype")
public class VerifyVariableListener extends AbstractExecution {

	@Setter
	private Expression variableName;

	@Override
	protected void execute() {
		getVariable(evaluate(this.variableName));
	}

}

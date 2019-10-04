package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.BASKET_ID_VAR;
import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;

import org.camunda.bpm.engine.spring.annotations.ProcessVariable;
import org.camunda.bpm.engine.spring.annotations.StartProcess;
import org.springframework.stereotype.Service;

@Service
public class CheckoutFlow {

	@StartProcess(processKey = PROCESS_NAME, returnProcessInstanceId = true)
	public String start(@ProcessVariable(BASKET_ID_VAR) UUID basketId) {
		return null;
	}
}

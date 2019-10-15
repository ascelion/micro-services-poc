package ascelion.micro.flow;

import java.util.Map;
import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutFlow {
	static private final Logger LOG = loggerForThisClass();

	@Autowired
	private ProcessEngine camunda;

	public ProcessInstance start(UUID basketId, String authz) {
		final Map<String, Object> variables = Variables.createVariables()
				.putValue(AUTHORIZATION, authz);

		LOG.info("Basket[{}]: checking out", basketId);

		return this.camunda.getRuntimeService()
				.startProcessInstanceByKey(PROCESS_NAME, basketId.toString(), variables);
	}
}

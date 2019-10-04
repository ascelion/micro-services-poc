package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_VERIFY_LISTENER;

import org.springframework.stereotype.Service;

@Service(CUSTOMER_VERIFY_LISTENER)
public class CustomerVerifyListener extends AbstractExecutionListener {

	@Override
	protected void execute(UUID pid) {
		getVariable(CUSTOMER_RESPONSE_VAR);
	}

}

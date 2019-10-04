package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.BASKET_VERIFY_LISTENER;

import org.springframework.stereotype.Service;

@Service(BASKET_VERIFY_LISTENER)
public class BasketVerifyListener extends AbstractExecutionListener {

	@Override
	protected void execute(UUID pid) {
		getVariable(BASKET_RESPONSE_VAR);
	}

}

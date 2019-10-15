package ascelion.micro.flow;

import ascelion.micro.customer.api.Customer;

import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.SHIPPING_REQUEST_TASK;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import org.slf4j.Logger;

@Action(SHIPPING_REQUEST_TASK)
public class ShipItemsTask extends AbstractExecution {
	static private final Logger LOG = loggerForThisClass();

	@Override
	protected void execute() {
		final Customer cust = getVariable(CUSTOMER_RESPONSE_VAR);

		LOG.info("Basket[{}]: shipping to {}", basketId(), cust.getAddresses().get(0).getId());
	}
}

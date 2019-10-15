package ascelion.micro.flow;

import ascelion.micro.customer.api.Customer;

import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.SEND_INVOICE_TASK;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import org.slf4j.Logger;

@Action(SEND_INVOICE_TASK)
public class SendInvoiceTask extends AbstractExecution {
	static private final Logger LOG = loggerForThisClass();

	@Override
	protected void execute() {
		final Customer cust = getVariable(CUSTOMER_RESPONSE_VAR);

		LOG.info("Basket[{}]: sendind invoice to {}", basketId(), cust.getEmail());
	}
}

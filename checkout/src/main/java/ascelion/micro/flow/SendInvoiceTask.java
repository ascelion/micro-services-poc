package ascelion.micro.flow;

import static ascelion.micro.flow.CheckoutConstants.SEND_INVOICE_TASK;

import org.springframework.stereotype.Service;

@Service(SEND_INVOICE_TASK)
public class SendInvoiceTask extends AbstractExecution {
	@Override
	protected void execute() {
	}
}

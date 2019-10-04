package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.SEND_INVOICE_TASK;

import org.springframework.stereotype.Service;

@Service(SEND_INVOICE_TASK)
public class SendInvoiceTask extends AbstractTask {
	@Override
	protected void execute(UUID pid) {
	}
}

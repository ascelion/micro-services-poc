package ascelion.micro.flow;

import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REFUND_TASK;

import org.springframework.stereotype.Service;

@Service(PAYMENT_REFUND_TASK)
public class PaymentRefundTask extends AbstractExecution {
	@Override
	protected void execute() {
	}
}

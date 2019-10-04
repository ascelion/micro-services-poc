package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REFUND_TASK;

import org.springframework.stereotype.Service;

@Service(PAYMENT_REFUND_TASK)
public class PaymentRefundTask extends AbstractTask {
	@Override
	protected void execute(UUID pid) {
	}
}

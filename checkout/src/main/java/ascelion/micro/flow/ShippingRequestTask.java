package ascelion.micro.flow;

import java.util.UUID;

import static ascelion.micro.flow.CheckoutConstants.SHIPPING_REQUEST_TASK;

import org.springframework.stereotype.Service;

@Service(SHIPPING_REQUEST_TASK)
public class ShippingRequestTask extends AbstractTask {
	@Override
	protected void execute(UUID pid) {
	}
}

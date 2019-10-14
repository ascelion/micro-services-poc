package ascelion.micro.flow;

import static ascelion.micro.flow.CheckoutConstants.SHIPPING_REQUEST_TASK;

import org.springframework.stereotype.Service;

@Service(SHIPPING_REQUEST_TASK)
public class ShipItemsTask extends AbstractExecution {
	@Override
	protected void execute() {
	}
}

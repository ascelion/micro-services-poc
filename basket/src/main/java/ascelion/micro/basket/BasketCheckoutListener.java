package ascelion.micro.basket;

import java.util.UUID;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.Basket.Status;
import ascelion.micro.checkout.api.CheckoutChannel;
import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CheckoutChannel.class)
public class BasketCheckoutListener {

	@Autowired
	private BasketRepo repo;
	@Autowired
	private CheckoutMessageSender<Basket> cms;

	@StreamListener(target = CheckoutChannel.INPUT, condition = "headers." + HEADER_KIND + " == '" + BASKET_MESSAGE + "_REQUEST'")
	public void messageReceived(@Payload MessagePayload<UUID> payload, @Header(HEADER_CORRELATION) UUID pid) {
		final var basketId = payload.get();

		var basket = this.repo.findById(basketId)
				.filter(b -> b.getStatus() != Status.FINALIZED)
				.orElse(null);

		if (basket != null) {
			switch (basket.getStatus()) {
			case CONSTRUCT:
				if (basket.pruneItems() > 0) {
					this.repo.save(basket.advance());
				} else {
					basket = null;
				}
				break;

			default:
				this.repo.save(basket.advance());
				break;
			}
		}

		this.cms.send(Direction.RESPONSE, pid, BASKET_MESSAGE, MessagePayload.of(basket));
	}
}

package ascelion.micro.basket;

import java.util.UUID;

import ascelion.micro.basket.api.Basket;
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
	public void getBasket(
			@Payload MessagePayload<Basket.Status> payload,
			@Header(HEADER_CORRELATION) UUID basketId) {

		final var status = payload.orElse(null);
		var basket = this.repo.findById(basketId)
				.orElse(null);

		if (basket != null) {
			if (status == null) {
				basket.setStatus(Basket.Status.ORDERING);
			} else {
				basket.setStatus(status);
			}

			basket = this.repo.save(basket);
		}

		if (status == null) {
			this.cms.send(Direction.RESPONSE, basketId, BASKET_MESSAGE, MessagePayload.of(basket));
		}
	}
}

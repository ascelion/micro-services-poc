package ascelion.micro.basket;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.BasketItem;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.shared.model.EntityUtil;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class MappingsTest {

	private final BeanToBeanMapper bbm = new BeanToBeanMapper();

	@Before
	public void setUp() {
		this.bbm.afterPropertiesSet();
	}

	@Test
	public void BasketItem_To_ReservationRequest() {
		final var item = BasketItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final var basket = Basket.builder()
				.customerId(randomUUID())
				.build();

		basket.merge(item);

		EntityUtil.populate(basket, item);

		final var req = this.bbm.create(ReservationRequest.class, item);

		assertThat(req.getOwnerId(), equalTo(basket.getId()));
		assertThat(req.getProductId(), equalTo(item.getProductId()));
		assertThat(req.getQuantity(), equalTo(item.getQuantity()));
	}

	@Test
	public void BasketRequest_To_Basket() {
		final var request = new BasketRequest(randomUUID());
		final var basket = this.bbm.create(Basket.class, request);

		assertThat(basket.getCustomerId(), equalTo(request.getCustomerId()));
	}

	@Test
	public void BasketItemRequest_To_BasketItem() {
		final var request = new BasketItemRequest(randomUUID(), randomDecimal(10, 20));
		final var basket = this.bbm.create(BasketItem.class, request);

		assertThat(basket.getProductId(), equalTo(request.getProductId()));
		assertThat(basket.getQuantity(), equalTo(request.getQuantity()));
	}

}

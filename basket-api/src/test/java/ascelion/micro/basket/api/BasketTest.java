package ascelion.micro.basket.api;

import java.io.IOException;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

public class BasketTest {

	@Test
	public void relink() throws IOException {
		final BasketItem item0 = BasketItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final BasketItem item1 = BasketItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final Basket basket1 = Basket.builder()
				.customerId(randomUUID())
				.build();

		item0.setId(randomUUID());
		item1.setId(randomUUID());
		basket1.setId(randomUUID());

		basket1.merge(item0, item1);

		final ObjectMapper om = new ObjectMapper();

		om.enable(SerializationFeature.INDENT_OUTPUT);

		final String buf = om.writeValueAsString(basket1);

		System.out.println(buf);

		final Basket basket2 = om.readValue(buf, Basket.class);

		assertThat(basket2.beq(basket1), is(true));
		assertThat(basket2.getItems().get(0).getBasket(), is(sameInstance(basket2)));
		assertThat(basket2.getItems().get(1).getBasket(), is(sameInstance(basket2)));
	}

}

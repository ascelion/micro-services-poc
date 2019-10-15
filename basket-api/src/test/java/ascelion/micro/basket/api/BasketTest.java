package ascelion.micro.basket.api;

import java.io.IOException;

import ascelion.micro.shared.endpoint.Search;
import ascelion.micro.shared.model.EntityUtil;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

public class BasketTest {

	private final ObjectMapper om = new ObjectMapper();

	@Before
	public void setUp() {
		this.om.findAndRegisterModules();
		this.om.enable(SerializationFeature.INDENT_OUTPUT);
		this.om.disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);
	}

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

		EntityUtil.populate(item0, item1, basket1);

		basket1.merge(item0, item1);

		final String buf = this.om.writeValueAsString(basket1);

		System.out.println(buf);

		final Basket basket2 = this.om.readValue(buf, Basket.class);

		assertThat(basket2.beq(basket1), is(true));
		assertThat(basket2.getItems().get(0).getBasket(), is(sameInstance(basket2)));
		assertThat(basket2.getItems().get(1).getBasket(), is(sameInstance(basket2)));
	}

	@Test
	public void search() throws IOException {
		final var basket = Basket.builder().status(Basket.Status.CONSTRUCT).build();
		final var search = new Search<>(basket, null, null, null, false);
		final var type = new TypeReference<Search<Basket>>() {
		};

		final var buf = this.om.writeValueAsString(search);

		System.out.println(buf);

		final var search2 = this.om.readValue(buf, type);

		assertThat(search2, notNullValue());
	}
}

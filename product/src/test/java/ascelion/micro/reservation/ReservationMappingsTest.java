package ascelion.micro.reservation;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.product.api.Product;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.shared.model.EntityUtil;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ReservationMappingsTest {

	private final BeanToBeanMapper bbm = new BeanToBeanMapper();

	@Before
	public void setUp() {
		this.bbm.afterPropertiesSet();
	}

	@Test
	public void Reservation_To_ReservationResponse() {
		final var prod = Product.builder()
				.price(randomDecimal(10, 20))
				.stock(randomDecimal(1000, 2000))
				.build();
		final var res = Reservation.builder()
				.product(prod)
				.ownerId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();

		EntityUtil.populate(prod, res);

		final var rsp = this.bbm.create(ReservationResponse.class, res);

		assertThat(rsp, notNullValue());
		assertThat(rsp.getPrice(), equalTo(prod.getPrice()));
		assertThat(rsp.getQuantity(), equalTo(res.getQuantity()));
	}

}

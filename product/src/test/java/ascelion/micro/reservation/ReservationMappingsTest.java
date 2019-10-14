package ascelion.micro.reservation;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.product.ProductRepo;
import ascelion.micro.product.api.Product;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.shared.model.AbstractEntity;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({
		BeanToBeanMapper.class,
		ReservationEndpoint.class,
})
public class ReservationMappingsTest {

	@Autowired
	private final BeanToBeanMapper bbm = new BeanToBeanMapper(true);

	@MockBean
	private ReservationService resServ;
	@MockBean
	private ReservationRepo resRepo;
	@MockBean
	private ProductRepo prdRepo;

	@Test
	public void mapReservationToReservationResponse() {
		final var prod = Product.builder()
				.price(randomDecimal(10, 20))
				.stock(randomDecimal(1000, 2000))
				.build();
		final var res = Reservation.builder()
				.product(prod)
				.ownerId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();

		AbstractEntity.populate(prod);
		AbstractEntity.populate(res);

		final var rsp = this.bbm.create(ReservationResponse.class, res);

		assertThat(rsp, notNullValue());
		assertThat(rsp.getPrice(), equalTo(prod.getPrice()));
		assertThat(rsp.getQuantity(), equalTo(res.getQuantity()));
	}

}

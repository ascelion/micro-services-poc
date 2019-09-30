package ascelion.micro.reservations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.products.Product;
import ascelion.micro.products.ProductsRepoIT;
import ascelion.micro.products.ProductsRepository;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
public class ReservationsRepoIT {

	@Autowired
	private EntityManager tem;

	@Autowired
	private ProductsRepository prdRepo;
	@Autowired
	private ReservationsRepository resRepo;

	@Before
	public void setUp() {
		this.tem.setFlushMode(FlushModeType.AUTO);
	}

	@Test
	@Transactional
	public void product_availability() {
		final Product p = ProductsRepoIT.newProduct();

		this.prdRepo.saveAndFlush(p);
		this.tem.detach(p);

		final BigDecimal a0 = this.prdRepo.stockAvailability(p);

		assertThat(a0, equalTo(p.getStock()));

		final Reservation r = Reservation.builder()
				.product(p)
				.ownerId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();

		this.resRepo.saveAndFlush(r);
		this.tem.detach(r);

		final BigDecimal a1 = this.prdRepo.stockAvailability(p);

		assertThat(a1, lessThan(a0));
		assertThat(a1, equalTo(p.getStock().subtract(r.getQuantity())));

		final List<Reservation> old = this.resRepo.findOlderThan(LocalDateTime.now().plusDays(1));

		this.resRepo.deleteInBatch(old);

		final BigDecimal a2 = this.prdRepo.stockAvailability(p);

		assertThat(a2, equalTo(p.getStock()));
	}
}

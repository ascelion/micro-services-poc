package ascelion.micro.product;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.product.ProductRepo;
import ascelion.micro.product.api.Product;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
public class ProductRepoIT {

	static public Product newProduct() {
		return Product.builder()
				.name(randomAscii(10, 20))
				.description(randomAscii(10, 20))
				.price(randomDecimal(100, 200))
				.stock(randomDecimal(100, 200))
				.build();
	}

	@Autowired
	private EntityManager tem;

	@Autowired
	private ProductRepo prdRepo;

	@Before
	public void setUp() {
		this.tem.setFlushMode(FlushModeType.AUTO);
	}

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_product_mappings() {
		final Product p1 = newProduct();

		this.prdRepo.saveAndFlush(p1);
		this.tem.detach(p1);

		assertThat(p1.getId(), notNullValue());

		final Optional<Product> p2o = this.prdRepo.findById(p1.getId());

		assertThat(p2o.isPresent(), is(true));

		final Product p2 = p2o.get();

		assertThat(p2, not(sameInstance(p1)));
		assertThat(p2.beq(p1), is(true));
	}

}

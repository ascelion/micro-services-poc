package ascelion.micro.products;

import java.util.Optional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@ActiveProfiles({ "itest", "dev" })
@SpringBootTest
public class ProductEntityIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private ProductsRepository repo;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final Product p1 = Products.generateOne(1, false);

		this.tem.persist(p1);
		this.tem.flush();
		this.tem.detach(p1);

		assertThat(p1.getId(), notNullValue());

		final Optional<Product> p2o = this.repo.findById(p1.getId());

		assertThat(p2o.isPresent(), is(true));

		final Product p2 = p2o.get();

		assertThat(p2, not(sameInstance(p1)));

		assertThat(p2.getId(), notNullValue());
		assertThat(p2.getCreatedAt(), notNullValue());
		assertThat(p2.getUpdatedAt(), notNullValue());
		assertThat(p2.getName(), equalTo(p1.getName()));
		assertThat(p2.getDescription(), equalTo(p1.getDescription()));
		assertThat(p2.getPrice(), equalTo(p1.getPrice().setScale(2)));

		assertThat(p2.getPrice().scale(), equalTo(2));
	}
}

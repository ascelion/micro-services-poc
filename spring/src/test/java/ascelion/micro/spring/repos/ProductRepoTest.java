package ascelion.micro.spring.repos;

import java.util.Optional;

import javax.persistence.EntityManager;

import ascelion.micro.spring.model.Product;
import ascelion.micro.spring.model.Products;
import ascelion.micro.spring.repo.ProductRepo;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepoTest {
	@Autowired
	private EntityManager tem;

	@Autowired
	private ProductRepo repo;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	public void check_table_structure() {
		final Product p1 = Products.generateOne(1, false);

		this.tem.persist(p1);
		this.tem.flush();
		this.tem.detach(p1);

		assertThat(p1.getId(), notNullValue());

		final Optional<Product> p2o = this.repo.findById(p1.getId());

		assertThat(p2o.isPresent(), is(true));

		final Product p2 = p2o.get();

		assertThat(p2, not(sameInstance(p1)));

		assertThat(p2.getName(), equalTo(p1.getName()));
		assertThat(p2.getDescription(), equalTo(p1.getDescription()));
		assertThat(p2.getCurrentPrice(), equalTo(p1.getCurrentPrice().setScale(2)));
		assertThat(p2.getLastUpdate(), notNullValue());

		assertThat(p2.getCurrentPrice().scale(), equalTo(2));
	}
}

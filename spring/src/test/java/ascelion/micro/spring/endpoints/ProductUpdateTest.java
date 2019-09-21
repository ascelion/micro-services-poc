package ascelion.micro.spring.endpoints;

import java.math.BigDecimal;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import ascelion.micro.spring.endpoint.ProductUpdate;
import ascelion.validation.OnCreate;
import ascelion.validation.OnUpdate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProductUpdateTest {

	private static final BigDecimal PRICE = new BigDecimal(3.14);
	private final Validator bv = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void invalid() {
		assertThat(this.bv.validate(new ProductUpdate(null, null, null), Default.class, OnCreate.class),
		        hasSize(3));
		assertThat(this.bv.validate(new ProductUpdate(null, null, null), Default.class, OnUpdate.class),
		        hasSize(1));

		assertThat(this.bv.validate(new ProductUpdate("", null, null), Default.class, OnCreate.class),
		        hasSize(3));
		assertThat(this.bv.validate(new ProductUpdate("", null, null), Default.class, OnUpdate.class),
		        hasSize(2));

		assertThat(this.bv.validate(new ProductUpdate(null, "", null), Default.class, OnCreate.class),
		        hasSize(3));
		assertThat(this.bv.validate(new ProductUpdate(null, "", null), Default.class, OnUpdate.class),
		        hasSize(2));

		assertThat(this.bv.validate(new ProductUpdate(null, "ABCDEF", null), Default.class, OnCreate.class),
		        hasSize(3));
		assertThat(this.bv.validate(new ProductUpdate(null, "ABCDEF", null), Default.class, OnUpdate.class),
		        hasSize(1));

		assertThat(this.bv.validate(new ProductUpdate("A", null, PRICE.negate()), Default.class, OnCreate.class),
		        hasSize(2));
		assertThat(this.bv.validate(new ProductUpdate("A", null, PRICE.negate()), Default.class, OnUpdate.class),
		        hasSize(1));
	}

	@Test
	public void validWithAny() {
		assertThat(this.bv.validate(new ProductUpdate("A", null, null)),
		        hasSize(0));
		assertThat(this.bv.validate(new ProductUpdate(null, "0123456789", null)),
		        hasSize(0));
		assertThat(this.bv.validate(new ProductUpdate(null, null, PRICE)),
		        hasSize(0));
	}

}

package ascelion.micro.products;

import java.math.BigDecimal;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import ascelion.micro.validation.OnCreate;
import ascelion.micro.validation.OnPatch;
import ascelion.micro.validation.OnUpdate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProductValidationTest {

	private static final BigDecimal PRICE = new BigDecimal(3.14);
	private final Validator bv = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void invalid() {
		assertThat(this.bv.validate(new ProductRequest(null, null, null), Default.class, OnCreate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, null, null), Default.class, OnUpdate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, null, null), Default.class, OnPatch.class),
				hasSize(1));

		assertThat(this.bv.validate(new ProductRequest("", null, null), Default.class, OnCreate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest("", null, null), Default.class, OnUpdate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest("", null, null), Default.class, OnPatch.class),
				hasSize(2));

		assertThat(this.bv.validate(new ProductRequest(null, "", null), Default.class, OnCreate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, "", null), Default.class, OnUpdate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, "", null), Default.class, OnPatch.class),
				hasSize(2));

		assertThat(this.bv.validate(new ProductRequest(null, "ABCDEF", null), Default.class, OnCreate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, "ABCDEF", null), Default.class, OnUpdate.class),
				hasSize(3));
		assertThat(this.bv.validate(new ProductRequest(null, "ABCDEF", null), Default.class, OnPatch.class),
				hasSize(1));

		assertThat(this.bv.validate(new ProductRequest("A", null, PRICE.negate()), Default.class, OnCreate.class),
				hasSize(2));
		assertThat(this.bv.validate(new ProductRequest("A", null, PRICE.negate()), Default.class, OnUpdate.class),
				hasSize(2));
		assertThat(this.bv.validate(new ProductRequest("A", null, PRICE.negate()), Default.class, OnPatch.class),
				hasSize(1));
	}

	@Test
	public void validWithAny() {
		assertThat(this.bv.validate(new ProductRequest("A", null, null)),
				hasSize(0));
		assertThat(this.bv.validate(new ProductRequest(null, "0123456789", null)),
				hasSize(0));
		assertThat(this.bv.validate(new ProductRequest(null, null, PRICE)),
				hasSize(0));
	}

}

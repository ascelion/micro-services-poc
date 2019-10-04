package ascelion.micro.product;

import java.math.BigDecimal;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import ascelion.micro.product.api.ProductRequest;
import ascelion.micro.shared.validation.OnCreate;
import ascelion.micro.shared.validation.OnPatch;
import ascelion.micro.shared.validation.OnUpdate;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProductValidationTest {

	private static final BigDecimal PRICE = new BigDecimal(3.14);
	private final Validator bv = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void invalid() {
		final ProductRequest p0 = ProductRequest.builder().build();
		assertThat(this.bv.validate(p0, Default.class, OnCreate.class), hasSize(4));
		assertThat(this.bv.validate(p0, Default.class, OnUpdate.class), hasSize(4));
		assertThat(this.bv.validate(p0, Default.class, OnPatch.class), hasSize(1));

		final ProductRequest p1 = ProductRequest.builder().name("").build();
		assertThat(this.bv.validate(p1, Default.class, OnCreate.class), hasSize(4));
		assertThat(this.bv.validate(p1, Default.class, OnUpdate.class), hasSize(4));
		assertThat(this.bv.validate(p1, Default.class, OnPatch.class), hasSize(2));

		final ProductRequest p2 = ProductRequest.builder().description("").build();
		assertThat(this.bv.validate(p2, Default.class, OnCreate.class), hasSize(4));
		assertThat(this.bv.validate(p2, Default.class, OnUpdate.class), hasSize(4));
		assertThat(this.bv.validate(p2, Default.class, OnPatch.class), hasSize(2));

		final ProductRequest p3 = ProductRequest.builder().description("ABCDEF").build();
		assertThat(this.bv.validate(p3, Default.class, OnCreate.class), hasSize(4));
		assertThat(this.bv.validate(p3, Default.class, OnUpdate.class), hasSize(4));
		assertThat(this.bv.validate(p3, Default.class, OnPatch.class), hasSize(1));

		final ProductRequest p4 = ProductRequest.builder().price(randomDecimal(10, 20).negate()).build();
		assertThat(this.bv.validate(p4, Default.class, OnCreate.class), hasSize(4));
		assertThat(this.bv.validate(p4, Default.class, OnUpdate.class), hasSize(4));
		assertThat(this.bv.validate(p4, Default.class, OnPatch.class), hasSize(1));
	}

	@Test
	public void validWithAny() {
		assertThat(this.bv.validate(ProductRequest.builder().name(randomAscii(1, 20)).build()), hasSize(0));
		assertThat(this.bv.validate(ProductRequest.builder().description(randomAscii(10, 20)).build()), hasSize(0));
		assertThat(this.bv.validate(ProductRequest.builder().price(randomDecimal(10, 20)).build()), hasSize(0));
		assertThat(this.bv.validate(ProductRequest.builder().stock(randomDecimal(10, 20)).build()), hasSize(0));
	}

}

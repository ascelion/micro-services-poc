package ascelion.micro.mapper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
		BeanToBeanMapper.class,
})
@ActiveProfiles("test")
public class BeanToBeanMapperTest {
	@BBMap(to = Bean2.class)
	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	@Getter
	static public class Bean1 {
		private String field1;
		private String field2;
	}

	static public class Bean2 {
	}

	@BBMap(from = Bean1.class, bidi = true,
			fields = {
					@BBField(from = "field1", to = "b.field1"),
					@BBField(from = "field2", to = "b.field2"),
			})
	@NoArgsConstructor
	@AllArgsConstructor
	@Setter
	@Getter
	static public class Bean3 {
		private Bean1 b;
	}

	@Configuration
	@BBMap(from = Bean2.class, to = Bean3.class)
	static public class Mappings {
	}

	@Autowired
	private BeanToBeanMapper bbm;

	@Test
	public void bean3_to_bean1_create() {
		final var b = this.bbm.create(Bean1.class, new Bean3(new Bean1("value1", "value2")));

		assertThat(b.field1, equalTo("value1"));
		assertThat(b.field2, equalTo("value2"));
	}

	@Test
	public void bean1_to_bean3_create() {
		final var b = this.bbm.create(Bean3.class, new Bean1("value1", "value2"));

		assertThat(b.b.field1, equalTo("value1"));
		assertThat(b.b.field2, equalTo("value2"));
	}

	@Test
	public void bean3_to_bean1_with_nulls() {
		final var b3 = new Bean3(new Bean1("value1", null));
		final var b1 = new Bean1("old1", "old2");

		this.bbm.copyWithNulls(b1, b3);

		assertThat(b1.field1, equalTo("value1"));
		assertThat(b1.field2, nullValue());
	}

	@Test
	public void bean1_to_bean3_with_nulls() {
		final var b1 = new Bean1("value1", null);
		final var b3 = new Bean3(new Bean1("old1", "old2"));

		this.bbm.copyWithNulls(b3, b1);

		assertThat(b3.b.field1, equalTo("value1"));
		assertThat(b3.b.field2, nullValue());
	}

	@Test
	public void bean3_to_bean1_without_nulls() {
		final var b3 = new Bean3(new Bean1("value1", null));
		final var b1 = new Bean1("old1", "old2");

		this.bbm.copyWithoutNulls(b1, b3);

		assertThat(b1.field1, equalTo("value1"));
		assertThat(b1.field2, equalTo("old2"));
	}

	@Test
	public void bean1_to_bean3_without_nulls() {
		final var b1 = new Bean1("value1", null);
		final var b3 = new Bean3(new Bean1("old1", "old2"));

		this.bbm.copyWithoutNulls(b3, b1);

		assertThat(b3.b.field1, equalTo("value1"));
		assertThat(b3.b.field2, equalTo("old2"));
	}

}

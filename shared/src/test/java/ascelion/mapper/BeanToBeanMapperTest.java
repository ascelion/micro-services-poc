package ascelion.mapper;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;

import static org.hamcrest.Matchers.equalTo;
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
	}

	static public class Bean2 {
	}

	@BBMap(from = Bean1.class, bidi = true,
			fields = {
					@BBField(from = "field1", to = "b.field1"),
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
	public void bean3_to_bean1() {
		final Bean1 b = this.bbm.create(Bean1.class, new Bean3(new Bean1("value1")));

		assertThat(b.field1, equalTo("value1"));
	}

	@Test
	public void bean1_to_bean3() {
		final Bean3 b = this.bbm.create(Bean3.class, new Bean1("value1"));

		assertThat(b.b.field1, equalTo("value1"));
	}

}

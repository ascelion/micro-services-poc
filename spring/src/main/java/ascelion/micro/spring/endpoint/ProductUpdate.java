package ascelion.micro.spring.endpoint;

import java.math.BigDecimal;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.validation.OnCreate;
import ascelion.validation.OnUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * DTO used by endpoint.
 */
@RequiredArgsConstructor
@Getter
public class ProductUpdate {
	@Size(min = 1, max = 250, message = "{product.invalid.name}")
	@NotNull(groups = OnCreate.class)
	private final String name;

	@Size(min = 10, message = "{product.invalid.description}")
	@NotNull(groups = OnCreate.class)
	private final String description;

	@Min(value = 0, message = "{product.invalid.price}")
	@NotNull(groups = OnCreate.class)
	private final BigDecimal price;

	@AssertTrue(groups = OnUpdate.class, message = "{product.invalid.empty}")
	@JsonIgnore
	public boolean getAtLeastOne() {
		return !StringUtils.isEmpty(this.name) || !StringUtils.isEmpty(this.description) || this.price != null;
	}
}

package ascelion.micro.shared.endpoint;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.POJO;
import ascelion.micro.shared.model.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Search<E extends AbstractEntity<E>> extends POJO {
	@NotNull
	public final E probe;
	public final String[] sort;
	@Min(0)
	public final Integer page;
	@Min(10)
	public final Integer size;

	public final boolean any;

	@AssertTrue
	@JsonIgnore
	public boolean isPageValid() {
		return this.page != null ? this.size != null : true;
	}
}

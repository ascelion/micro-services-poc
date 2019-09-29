package ascelion.micro.shared.endpoint;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.model.AbstractEntity;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ViewEntityEndpoint<T extends AbstractEntity<T>> {
	@ApiOperation("Get all entities")
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	List<T> getEntities(
	//@formatter:off
	        @ApiParam("Optional parameter to get a specific page of entities")
	        @RequestParam(name = "page", required = false)
	        @Min(0) Integer page,

	        @ApiParam("The size of the page if a page is requested")
	        @RequestParam(name = "size", required = false, defaultValue = "10")
	        @Min(10) int size
	//@formatter:on
	);

	@ApiOperation("Get an entity by its identifier")
	@GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
	T getEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @PathVariable("id")
	        @NotNull UUID id
	//@formatter:on
	);

}

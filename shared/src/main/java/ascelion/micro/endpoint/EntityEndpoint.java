package ascelion.micro.endpoint;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ascelion.micro.model.AbstractEntity;

import io.swagger.annotations.ApiParam;

public interface EntityEndpoint<T extends AbstractEntity, U> {

	List<T> getEntities(
	//@formatter:off
	        @ApiParam("Optional parameter to get a specific page of products")
	        @Min(0) Integer page,

	        @ApiParam("The size of the page if a page is requested")
	        @Min(10) int size
	//@formatter:on
	);

	T getEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @Min(1) long id
	//@formatter:on
	);

	T createEntity(
	//@formatter:off
	        @ApiParam(value = "The entity data", required = true)
	        @NotNull @Valid U request
	//@formatter:on
	);

	T updateEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @Min(1) long id,

	        @ApiParam(value = "The entity data", required = true)
	        @NotNull @Valid U request
	//@formatter:on
	);

	T patchEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @Min(1) long id,

	        @ApiParam(value = "The entity data", required = true)
	        @NotNull @Valid U request
	//@formatter:on
	);
}

package ascelion.micro.endpoint;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ascelion.micro.model.AbstractEntity;

import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public abstract class ViewEntityEndpoint<T extends AbstractEntity> {
	protected final JpaRepository<T, UUID> repo;

	@ApiOperation("Get all entities")
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<T> getEntities(
	//@formatter:off
	        @ApiParam("Optional parameter to get a specific page of entities")
	        @RequestParam(name = "page", required = false)
	        @Min(0) Integer page,

	        @ApiParam("The size of the page if a page is requested")
	        @RequestParam(name = "size", required = false, defaultValue = "10")
	        @Min(10) int size ) {
	//@formatter:on

		return ofNullable(page)
				.map(p -> PageRequest.of(p, size))
				.map(this.repo::findAll)
				.map(Page::getContent)
				.orElseGet(this.repo::findAll);
	}

	@ApiOperation("Get an entity by its identifier")
	@GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
	public T getEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @PathVariable("id")
	        @NotNull UUID id ) {
	//@formatter:on

		return this.repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no such entity: " + id));
	}

}

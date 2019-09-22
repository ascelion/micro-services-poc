package ascelion.micro.endpoint;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import ascelion.micro.model.AbstractEntity;
import ascelion.micro.utils.Mappings;
import ascelion.micro.validation.OnCreate;
import ascelion.micro.validation.OnPatch;
import ascelion.micro.validation.OnUpdate;

import static java.util.Optional.ofNullable;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Validated
public abstract class EntityEndpoint<T extends AbstractEntity, U> {
	protected final JpaRepository<T, UUID> repo;
	private final Class<T> type = (Class<T>) resolveTypeArguments(getClass(), EntityEndpoint.class)[0];

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

	@ApiOperation("Create a new entity")
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	public T createEntity(
	//@formatter:off
	        @ApiParam(value = "The entity data", required = true)
	        @RequestBody
	        @NotNull @Valid U request ) {
	//@formatter:on

		return this.repo.save(Mappings.copyProperties(request, this::newInstance, false));
	}

	@ApiOperation("Update an existing entity")
	@PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	// @PreAuthorize(Main.IS_ADMIN)
	@Validated({ Default.class, OnUpdate.class })
	public T updateEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @PathVariable("id")
	        @NotNull UUID id,

	        @ApiParam(value = "The entity data", required = true)
	        @RequestBody
	        @NotNull @Valid U request) {
	//@formatter:on

		return this.repo.findById(id)
				.map(ent -> Mappings.copyProperties(request, ent, false))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such entity"));
	}

	@ApiOperation("Partially update an existing entity")
	@PatchMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	// @PreAuthorize(Main.IS_ADMIN)
	@Validated({ Default.class, OnPatch.class })
	public T patchEntity(
	//@formatter:off
	        @ApiParam(value = "The entity identifier", required = true)
	        @PathVariable("id")
	        @NotNull UUID id,

	        @ApiParam(value = "The entity data", required = true)
	        @RequestBody
	        @NotNull @Valid U patch) {
	//@formatter:on

		return this.repo.findById(id)
				.map(ent -> Mappings.copyProperties(patch, ent, true))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such entity"));
	}

	@SneakyThrows
	private T newInstance() {
		return this.type.newInstance();
	}
}

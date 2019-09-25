package ascelion.micro.shared.endpoint;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.utils.Mappings;
import ascelion.micro.shared.validation.OnCreate;
import ascelion.micro.shared.validation.OnPatch;
import ascelion.micro.shared.validation.OnUpdate;

import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class EntityEndpoint<T extends AbstractEntity, U> extends ViewEntityEndpoint<T> {
	private final Class<T> type = (Class<T>) resolveTypeArguments(getClass(), EntityEndpoint.class)[0];

	public EntityEndpoint(JpaRepository<T, UUID> repo) {
		super(repo);
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

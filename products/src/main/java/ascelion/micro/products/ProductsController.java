package ascelion.micro.products;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import ascelion.micro.Main;
import ascelion.micro.endpoint.EntityEndpoint;
import ascelion.micro.utils.Mappings;
import ascelion.micro.validation.OnCreate;
import ascelion.micro.validation.OnPatch;
import ascelion.micro.validation.OnUpdate;

import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Products endpoint implementation.
 */
@RestController
@Validated
@RequiredArgsConstructor
public class ProductsController implements EntityEndpoint<Product, ProductRequest> {

	private final ProductsRepository repo;

	/**
	 * Get all products (pagination is optional).
	 */
	@Override
	@ApiOperation("Get all products")
	@GetMapping(path = "/products", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(Main.IS_USER)
	public List<Product> getEntities(
	//@formatter:off
	        @ApiParam("Optional parameter to get a specific page of products")
	        @RequestParam(name = "page", required = false)
	        @Min(0) Integer page,

	        @ApiParam("The size of the page if a page is requested")
	        @RequestParam(name = "size", required = false, defaultValue = "10")
	        @Min(10) int size) {
	//@formatter:on

		return ofNullable(page)
				.map(p -> PageRequest.of(p, size))
				.map(this.repo::findAll)
				.map(Page::getContent)
				.orElseGet(this.repo::findAll);
	}

	/**
	 * Get a product by id.
	 */
	@Override
	@ApiOperation("Get a product by its identifier")
	@GetMapping(path = "/products/{id}", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(Main.IS_USER)
	public Product getEntity(
	//@formatter:off
	        @ApiParam(value = "The product identifier", required = true)
	        @PathVariable(name = "id")
	        @Min(1) long id) {
	//@formatter:on

		return this.repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such product"));
	}

	/**
	 * Create a new product.
	 */
	@Override
	@ApiOperation("Create a new product")
	@PostMapping(path = "/products", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(Main.IS_ADMIN)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	public Product createEntity(
	//@formatter:off
	        @ApiParam(value = "The product data", required = true)
	        @RequestBody
	        @NotNull @Valid ProductRequest request) {
	//@formatter:on

		return this.repo.save(Mappings.copyProperties(request, Product::new, false));
	}

	/**
	 * Update an existing product.
	 */
	@Override
	@ApiOperation("Update an existing product")
	@PutMapping(path = "/products/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(Main.IS_ADMIN)
	@Validated({ Default.class, OnUpdate.class })
	public Product updateEntity(
	//@formatter:off
	        @ApiParam(value = "The product identifier", required = true)
	        @PathVariable(name = "id")
	        @Min(1) long id,

	        @ApiParam(value = "The product data", required = true)
	        @RequestBody
	        @NotNull @Valid ProductRequest request) {
	//@formatter:on

		return this.repo.findById(id)
				.map(ent -> Mappings.copyProperties(request, ent, false))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such product"));
	}

	/**
	 * Update an existing product.
	 */
	@Override
	@ApiOperation("Partially update an existing product")
	@PatchMapping(path = "/products/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(Main.IS_ADMIN)
	@Validated({ Default.class, OnPatch.class })
	public Product patchEntity(
	//@formatter:off
	        @ApiParam(value = "The product identifier", required = true)
	        @PathVariable(name = "id")
	        @Min(1) long id,

	        @ApiParam(value = "The product data", required = true)
	        @RequestBody
	        @NotNull @Valid ProductRequest patch) {
	//@formatter:on

		return this.repo.findById(id)
				.map(ent -> Mappings.copyProperties(patch, ent, true))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such product"));
	}
}

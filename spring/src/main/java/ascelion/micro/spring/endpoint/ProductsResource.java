package ascelion.micro.spring.endpoint;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import ascelion.micro.spring.ProductsApplication;
import ascelion.micro.spring.model.Product;
import ascelion.micro.spring.repo.ProductRepo;
import ascelion.validation.OnCreate;
import ascelion.validation.OnUpdate;

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
public class ProductsResource {

	private final ProductRepo repo;

	/**
	 * Get all products (pagination is optional).
	 */
	@ApiOperation("Get all products")
	@GetMapping(path = "/products", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(ProductsApplication.IS_USER)
	public List<Product> getProducts(
	        @ApiParam("Optional parameter to get a specific page of products")
	        @RequestParam(name = "page", required = false)
	        @Min(0) Integer page,

	        @ApiParam("The size of the page if a page is requested")
	        @RequestParam(name = "size", required = false, defaultValue = "10")
	        @Min(10) Integer size) {

		return ofNullable(page)
		        .map(p -> PageRequest.of(p, size))
		        .map(this.repo::findAll)
		        .map(Page::getContent)
		        .orElseGet(this.repo::findAll);
	}

	/**
	 * Get a product by id.
	 */
	@ApiOperation("Get a product by its identifier")
	@GetMapping(path = "/products/{id}", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(ProductsApplication.IS_USER)
	public Product getProduct(
	        @ApiParam(value = "The product identifier", required = true)
	        @PathVariable(name = "id")
	        @Min(1) long id) {

		return this.repo.findById(id)
		        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such product"));
	}

	/**
	 * Create a new product.
	 */
	@ApiOperation("Create a new product")
	@PostMapping(path = "/products", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(ProductsApplication.IS_ADMIN)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	public Product createProduct(
	        @ApiParam(value = "The product data", required = true)
	        @RequestBody
	        @NotNull @Valid ProductUpdate patch) {

		return copyAndSave(new Product(), patch);
	}

	/**
	 * Update an existing product.
	 */
	@ApiOperation("Update an existing product")
	@PutMapping(path = "/products/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(ProductsApplication.IS_ADMIN)
	@Validated({ Default.class, OnUpdate.class })
	public Product updateProduct(
	        @ApiParam(value = "The product identifier", required = true)
	        @PathVariable(name = "id")
	        @Min(1) long id,

	        @ApiParam(value = "The product data", required = true)
	        @RequestBody
	        @NotNull @Valid ProductUpdate patch) {

		return this.repo.findById(id)
		        .map(prod -> copyAndSave(prod, patch))
		        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such product"));
	}

	private Product copyAndSave(Product prod, ProductUpdate patch) {
		ofNullable(patch.getName()).ifPresent(prod::setName);
		ofNullable(patch.getDescription()).ifPresent(prod::setDescription);
		ofNullable(patch.getPrice()).ifPresent(prod::setCurrentPrice);

		return this.repo.save(prod);
	}
}

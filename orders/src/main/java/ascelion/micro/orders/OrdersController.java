package ascelion.micro.orders;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpoint;
import ascelion.micro.shared.validation.OnCreate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Endpoint("orders")
public class OrdersController extends ViewEntityEndpoint<Order> {
	public OrdersController(JpaRepository<Order, UUID> repo) {
		super(repo);
	}

	@ApiOperation("Create a new order")
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	public Order createEntity(
	//@formatter:off
	        @ApiParam(value = "The order data", required = true)
	        @RequestBody
	        @NotNull @Valid OrderRequest request ) {
	//@formatter:on

		return this.repo.save(this.bbm.copy(request, Order.class, true));
	}
}

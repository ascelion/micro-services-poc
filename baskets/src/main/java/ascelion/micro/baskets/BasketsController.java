package ascelion.micro.baskets;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import ascelion.micro.reservations.ReservationRequest;
import ascelion.micro.reservations.ReservationsApi;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;
import ascelion.micro.shared.validation.OnCreate;

import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Endpoint("baskets")
public class BasketsController extends ViewEntityEndpointBase<Basket, BasketsRepo> {
	static private final Logger L = loggerForThisClass();

	@Autowired
	@Lazy
	private ReservationsApi client;

	@Autowired
	private BasketItemsRepo itmRepo;

	public BasketsController(BasketsRepo repo) {
		super(repo);
	}

	@ApiOperation("Create a new basket")
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	public Basket createBasket(
	//@formatter:off
	        @ApiParam(value = "The basket data", required = true)
	        @RequestBody
	        @NotNull @Valid BasketRequest request ) {
	//@formatter:on
		return this.repo.save(this.bbm.create(Basket.class, request));
	}

	@ApiOperation("Add a new items to the basket")
	@PostMapping(path = "{basketId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Validated({ Default.class, OnCreate.class })
	@Transactional
	public Basket addItems(@PathVariable("basketId") UUID basketId, @RequestBody @NotNull @Valid @Size(min = 1) BasketItemRequest[] requests) {
		final Basket basket = this.repo.getById(basketId);
		BasketItem[] items = this.bbm.createArray(BasketItem.class, requests);

		basket.addItems(items);

		final ReservationRequest[] reservations = this.bbm.createArray(ReservationRequest.class, basket.getItems());

		items = this.bbm.createArray(BasketItem.class, this.client.reserve(reservations));

		basket.setItems(items);

		return this.repo.save(basket);
	}

	@ApiOperation("Update the quantity of an item")
	@PatchMapping(path = "{itemId}", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
	@Transactional
	public Basket udateItem(@PathVariable("itemId") UUID itemId,
			@RequestParam(name = "quantity", required = true) @NotNull @Min(0) BigDecimal quantity) {
		final BasketItem item = this.itmRepo.getById(itemId);

		item.setQuantity(quantity);

		final ReservationRequest[] reservations = this.client.reserve(this.bbm.create(ReservationRequest.class, item));

		item.setQuantity(reservations[0].getQuantity());

		return this.repo.save(item.getBasket());
	}

	@ApiOperation("Remove an item from the basket")
	@DeleteMapping(path = "{itemId}", produces = APPLICATION_JSON_VALUE)
	@Transactional
	public Basket deleteItem(@PathVariable("itemId") UUID itemId) {
		final BasketItem item = this.itmRepo.getById(itemId);

		try {
			this.client.finalize(ReservationsApi.Finalize.DISCARD, this.bbm.create(ReservationRequest.class, item));
		} catch (final Exception e) {
			L.error("Could not discard reservation for {}", itemId, e);
		}

		item.getBasket().delItem(itemId);

		return this.repo.save(item.getBasket());
	}
}

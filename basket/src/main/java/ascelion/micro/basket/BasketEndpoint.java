package ascelion.micro.basket;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.BasketItem;
import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;
import ascelion.micro.shared.validation.OnCreate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import feign.FeignException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@BBMap(from = BasketItem.class, to = ReservationRequest.class, bidi = false, fields = {
		@BBField(from = "basket.id", to = "ownerId")
})
public class BasketEndpoint extends ViewEntityEndpointBase<Basket, BasketRepo> {
	@Autowired
	private BasketItemRepo itmRepo;
	@Autowired
	private ReservationsApi resApi;

	public BasketEndpoint(BasketRepo repo) {
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
	public Basket addItems(@PathVariable("basketId") UUID basketId,
			@RequestBody @NotNull @Valid @Size(min = 1) BasketItemRequest... requests) {
		final var basket = this.repo.getById(basketId);

		basket.merge(this.bbm.createArray(BasketItem.class, requests));

		final var prodIds = stream(requests).map(BasketItemRequest::getProductId).collect(toSet());
		final var updated = basket.getItems().stream().filter(item -> prodIds.contains(item.getProductId()))
				.toArray(BasketItem[]::new);

		final var reservations = this.resApi.reserve(
				this.bbm.createArray(ReservationRequest.class, updated));

		for (var k = 0; k < reservations.length; k++) {
			final BasketItem item = updated[k];

			item.setQuantity(reservations[k].getQuantity());
		}

		return this.repo.save(basket.pruneEmptpyItems());
	}

	@ApiOperation("Update the quantity of an item")
	@PatchMapping(path = "{itemId}", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
	@Transactional
	public Basket udateItem(@PathVariable("itemId") UUID itemId,
			@RequestParam(name = "quantity", required = true) @NotNull @Min(0) BigDecimal quantity) {
		final var item = this.itmRepo.getById(itemId);

		item.setQuantity(quantity);

		final var reservations = this.resApi.reserve(
				this.bbm.create(ReservationRequest.class, item));

		item.setQuantity(reservations[0].getQuantity());

		return this.repo.save(item.getBasket().pruneEmptpyItems());
	}

	@ApiOperation("Remove an item from the basket")
	@DeleteMapping(path = "{itemId}", produces = APPLICATION_JSON_VALUE)
	@Transactional
	public Basket deleteItem(@PathVariable("itemId") UUID itemId) {
		final var item = this.itmRepo.getById(itemId);

		try {
			this.resApi.update(ReservationsApi.Operation.DISCARD,
					this.bbm.create(ReservationRequest.class, item));
		} catch (final FeignException e) {
			if (e.status() != HttpStatus.NOT_FOUND.value()) {
				throw e;
			}
		}

		item.getBasket().delItem(itemId);

		return this.repo.save(item.getBasket().pruneEmptpyItems());
	}
}

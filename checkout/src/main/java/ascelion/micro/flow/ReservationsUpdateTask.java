package ascelion.micro.flow;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.BasketItem;
import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.reservation.api.ReservationsApi.Operation;
import ascelion.micro.shared.config.FeignClientConfig;

import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_UPDATE_TASK;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_VAR;
import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.Expression;
import org.slf4j.Logger;

@Action(RESERVATIONS_UPDATE_TASK)
@RequiredArgsConstructor
@BBMap(from = BasketItem.class, to = ReservationRequest.class, bidi = false, fields = {
		@BBField(from = "basket.id", to = "ownerId")
})
public class ReservationsUpdateTask extends AbstractExecution {
	static private final Logger LOG = loggerForThisClass();

	private final ReservationsApi resApi;
	private final BeanToBeanMapper bbm;

	@Setter
	private Expression operation;

	@Override
	protected void execute() {
		final Basket basket = getVariable(BASKET_RESPONSE_VAR);
		final var requests = this.bbm.createArray(ReservationRequest.class, basket.getItems());
		final var op = Operation.valueOf(evaluate(this.operation));

		LOG.info("Basket[{}]: setting reservation to {}", basketId(), op);

		final ReservationResponse[] responses;

		FeignClientConfig.setAuthorization(getVariable(AUTHORIZATION));

		try {
			responses = this.resApi.update(op, requests);
		} finally {
			FeignClientConfig.setAuthorization(null);
		}

		setVariable(RESERVATIONS_VAR, responses);
	}
}

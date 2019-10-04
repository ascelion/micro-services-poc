package ascelion.micro.flow;

import java.util.UUID;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.reservation.api.ReservationsApi.Operation;

import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.OPERATION_VAR;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_UPDATE_TASK;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_VAR;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service(RESERVATIONS_UPDATE_TASK)
@RequiredArgsConstructor
public class ReservationsUpdateTask extends AbstractTask {
	private final ReservationsApi resApi;
	private final BeanToBeanMapper bbm;

	@Override
	protected void execute(UUID pid) {
		final Basket basket = getVariable(BASKET_RESPONSE_VAR);
		final Operation operation = Operation.valueOf(getVariable(OPERATION_VAR));
		final ReservationRequest[] requests = this.bbm.createArray(ReservationRequest.class, basket.getItems());
		final ReservationResponse[] responses = this.resApi.update(operation, requests);

		setVariable(RESERVATIONS_VAR, responses);
	}
}

package ascelion.micro.flow;

import java.util.HashMap;
import java.util.Map;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.BasketItem;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.reservation.api.ReservationsApi.Operation;

import static ascelion.micro.flow.CheckoutConstants.BASKET_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_VAR;
import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.util.FieldUtils.setProtectedFieldValue;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReservationsUpdateTaskTest {
	private final BeanToBeanMapper bbm = new BeanToBeanMapper();
	private final Map<String, Object> variables = new HashMap<>();

	@Mock
	private DelegateExecution execution;
	@Mock
	private ReservationsApi api;
	@Mock
	private Expression operation;

	private ReservationsUpdateTask task;

	@Before
	public void setUp() {
		when(this.execution.getVariables())
				.thenReturn(this.variables);

		when(this.operation.getValue(any()))
				.thenReturn("LOCK");

		this.bbm.setBeanClassLoader(currentThread().getContextClassLoader());
		this.bbm.afterPropertiesSet();

		this.task = new ReservationsUpdateTask(this.api, this.bbm);
		this.task.setOperation(this.operation);

	}

	@Test
	public void run() throws Exception {
		final var item0 = BasketItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final var basket = Basket.builder()
				.customerId(randomUUID())
				.build();
		basket.merge(item0);

		setProtectedFieldValue("id", basket, randomUUID());
		setProtectedFieldValue("id", item0, randomUUID());

		this.variables.put(BASKET_RESPONSE_VAR, basket);
		this.variables.put(AUTHORIZATION, randomAscii(10, 20));

		this.task.execute(this.execution);

		final var cap = ArgumentCaptor.forClass(ReservationRequest.class);

		verify(this.api).update(eq(Operation.LOCK), cap.capture());

		final var requests = cap.getAllValues();

		assertThat(requests, notNullValue());

		final var req0 = requests.get(0);

		assertThat(req0.getOwnerId(), equalTo(basket.getId()));
		assertThat(req0.getProductId(), equalTo(item0.getProductId()));
		assertThat(req0.getQuantity(), equalTo(item0.getQuantity()));

		assertThat(this.variables, hasKey(RESERVATIONS_VAR));
	}
}

package ascelion.micro.flow;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.checkout.api.CheckoutChannel.CUSTOMER_MESSAGE;
import static ascelion.micro.checkout.api.CheckoutChannel.SHIPPING_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_STATUS_TASK;
import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REFUND_TASK;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_UPDATE_TASK;
import static ascelion.micro.flow.CheckoutConstants.SEND_INVOICE_TASK;
import static ascelion.micro.flow.CheckoutConstants.SHIPPING_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.SHIP_ITEMS_RESPONSE_VAR;
import static ascelion.micro.flow.CheckoutConstants.VERIFY_VARIABLE_LISTENER;
import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.extension.mockito.CamundaMockito.autoMock;
import static org.camunda.bpm.extension.mockito.CamundaMockito.registerInstance;
import static org.camunda.bpm.extension.mockito.CamundaMockito.verifyJavaDelegateMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@Deployment(resources = PROCESS_NAME + ".bpmn")
public class CheckoutProcessTest extends AbstractProcessEngineRuleTest {

	@Rule
	public final MockitoRule MOCK = MockitoJUnit.rule();

	@Mock
	private BasketStatusTask basketStatus;
	@Mock
	private ReservationsUpdateTask reservationUpdate;
	@Mock
	private VerifyVariableListener verifyVariable;

	@Before
	public void setUp() {
		autoMock(PROCESS_NAME + ".bpmn");

		registerInstance(BASKET_STATUS_TASK, this.basketStatus);
		registerInstance(RESERVATIONS_UPDATE_TASK, this.reservationUpdate);
		registerInstance(VERIFY_VARIABLE_LISTENER, this.verifyVariable);
	}

	@Test
	public void checkoutOK() throws Exception {
		final var basketId = randomUUID().toString();
		final var pi = runtimeService()
				.startProcessInstanceByKey(PROCESS_NAME, basketId);

		verify(this.basketStatus, times(1))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(BASKET_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(BASKET_MESSAGE + "_RESPONSE");
		verify(this.verifyVariable, times(1))
				.notify(any(DelegateExecution.class));

		verifyJavaDelegateMock(CUSTOMER_REQUEST_TASK).executed();
		verify(this.reservationUpdate, times(1))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(CUSTOMER_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(CUSTOMER_MESSAGE + "_RESPONSE");
		verify(this.verifyVariable, times(2))
				.notify(any(DelegateExecution.class));

		verifyJavaDelegateMock(PAYMENT_REQUEST_TASK).executed();
		verify(this.basketStatus, times(1))
				.notify(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(PAYMENT_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(PAYMENT_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(SEND_INVOICE_TASK).executed();
		verifyJavaDelegateMock(SHIPPING_REQUEST_TASK).executed();
		verify(this.reservationUpdate, times(2))
				.execute(any(DelegateExecution.class));
		verify(this.basketStatus, times(2))
				.notify(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(SHIPPING_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(SHIPPING_MESSAGE + "_RESPONSE", basketId, singletonMap(SHIP_ITEMS_RESPONSE_VAR, "OK"));
		verify(this.verifyVariable, times(3))
				.notify(any(DelegateExecution.class));
		verify(this.basketStatus, times(3))
				.notify(any(DelegateExecution.class));

		verifyJavaDelegateMock(PAYMENT_REFUND_TASK).executed(never());

		assertThat(pi).isEnded();
	}

	@Test
	public void checkoutNOK() throws Exception {
		final var basketId = randomUUID().toString();
		final var pi = runtimeService()
				.startProcessInstanceByKey(PROCESS_NAME, basketId);

		verify(this.basketStatus, times(1))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(BASKET_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(BASKET_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(CUSTOMER_REQUEST_TASK).executed();
		verify(this.reservationUpdate, times(1))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(CUSTOMER_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(CUSTOMER_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(PAYMENT_REQUEST_TASK).executed();

		assertThat(pi).isWaitingFor(PAYMENT_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(PAYMENT_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(SEND_INVOICE_TASK).executed();
		verifyJavaDelegateMock(SHIPPING_REQUEST_TASK).executed();
		verify(this.reservationUpdate, times(2))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isWaitingFor(SHIPPING_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(SHIPPING_MESSAGE + "_RESPONSE", basketId, singletonMap(SHIP_ITEMS_RESPONSE_VAR, "NOK"));

		verifyJavaDelegateMock(PAYMENT_REFUND_TASK).executed();
		verify(this.reservationUpdate, times(3))
				.execute(any(DelegateExecution.class));

		assertThat(pi).isEnded();
	}
}

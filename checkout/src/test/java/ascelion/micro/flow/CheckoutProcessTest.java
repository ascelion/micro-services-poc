package ascelion.micro.flow;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.checkout.api.CheckoutChannel.CUSTOMER_MESSAGE;
import static ascelion.micro.checkout.api.CheckoutChannel.SHIPPING_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.BASKET_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.CUSTOMER_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REFUND_TASK;
import static ascelion.micro.flow.CheckoutConstants.PAYMENT_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;
import static ascelion.micro.flow.CheckoutConstants.RESERVATIONS_UPDATE_TASK;
import static ascelion.micro.flow.CheckoutConstants.SEND_INVOICE_TASK;
import static ascelion.micro.flow.CheckoutConstants.SHIPPING_REQUEST_TASK;
import static ascelion.micro.flow.CheckoutConstants.SHIP_ITEMS_RESPONSE_VAR;
import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.extension.mockito.CamundaMockito.autoMock;
import static org.camunda.bpm.extension.mockito.CamundaMockito.verifyJavaDelegateMock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@Deployment(resources = PROCESS_NAME + ".bpmn")
public class CheckoutProcessTest extends AbstractProcessEngineRuleTest {

	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();

	@Before
	public void setUp() {
		autoMock(PROCESS_NAME + ".bpmn");
	}

	@Test
	public void checkoutOK() {
		final var pi = runtimeService()
				.startProcessInstanceByKey(PROCESS_NAME, randomUUID().toString());

		verifyJavaDelegateMock(BASKET_REQUEST_TASK).executed();

		assertThat(pi).isWaitingFor(BASKET_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(BASKET_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(CUSTOMER_REQUEST_TASK).executed();
		verifyJavaDelegateMock(RESERVATIONS_UPDATE_TASK).executed();

		assertThat(pi).isWaitingFor(CUSTOMER_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(CUSTOMER_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(PAYMENT_REQUEST_TASK).executed();

		assertThat(pi).isWaitingFor(PAYMENT_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(PAYMENT_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(SEND_INVOICE_TASK).executed();
		verifyJavaDelegateMock(SHIPPING_REQUEST_TASK).executed();
		verifyJavaDelegateMock(RESERVATIONS_UPDATE_TASK).executed(times(2));

		assertThat(pi).isWaitingFor(SHIPPING_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(SHIPPING_MESSAGE + "_RESPONSE", pi.getBusinessKey(), singletonMap(SHIP_ITEMS_RESPONSE_VAR, "OK"));

		verifyJavaDelegateMock(PAYMENT_REFUND_TASK).executed(never());

		assertThat(pi).isEnded();
	}

	@Test
	public void checkoutNOK() {
		final var pi = runtimeService()
				.startProcessInstanceByKey(PROCESS_NAME, randomUUID().toString());

		verifyJavaDelegateMock(BASKET_REQUEST_TASK).executed();

		assertThat(pi).isWaitingFor(BASKET_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(BASKET_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(CUSTOMER_REQUEST_TASK).executed();
		verifyJavaDelegateMock(RESERVATIONS_UPDATE_TASK).executed();

		assertThat(pi).isWaitingFor(CUSTOMER_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(CUSTOMER_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(PAYMENT_REQUEST_TASK).executed();

		assertThat(pi).isWaitingFor(PAYMENT_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(PAYMENT_MESSAGE + "_RESPONSE");

		verifyJavaDelegateMock(SEND_INVOICE_TASK).executed();
		verifyJavaDelegateMock(SHIPPING_REQUEST_TASK).executed();
		verifyJavaDelegateMock(RESERVATIONS_UPDATE_TASK).executed(times(2));

		assertThat(pi).isWaitingFor(SHIPPING_MESSAGE + "_RESPONSE");
		runtimeService().correlateMessage(SHIPPING_MESSAGE + "_RESPONSE", pi.getBusinessKey(), singletonMap(SHIP_ITEMS_RESPONSE_VAR, "NOK"));

		verifyJavaDelegateMock(PAYMENT_REFUND_TASK).executed();
		verifyJavaDelegateMock(RESERVATIONS_UPDATE_TASK).executed(times(3));

		assertThat(pi).isEnded();
	}
}

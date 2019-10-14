package ascelion.micro.flow;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.camunda.JacksonVariableSerializer;
import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.payment.api.PaymentMessageSender;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.checkout.api.CheckoutChannel.BASKET_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_CORRELATION;
import static ascelion.micro.shared.message.MessageSenderAdapter.HEADER_KIND;
import static ascelion.micro.tests.RandomUtils.randomAscii;
import static java.util.UUID.randomUUID;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JpaEntityIT
@Deployment(resources = PROCESS_NAME + ".bpmn")
@ComponentScan(basePackageClasses = CheckoutFlow.class)
@Import({
//		CheckoutFlow.class,
		CheckoutMessageSender.class,
		PaymentMessageSender.class,
//		BasketRequestTask.class,
//		ProcessEngineConfig.class,
		JacksonVariableSerializer.class,
		BeanToBeanMapper.class,

//		CheckoutChannelMock.class
})
public class CheckoutProcessIT {

	@Rule
	public final ProcessEngineRule processEngine = new StandaloneInMemoryTestConfiguration().rule();

	@Autowired
	private CheckoutFlow checkout;
	@Autowired
	private CheckoutChannelMock checkoutChannelMock;
	@Autowired
	private BasketReceiveTask basketReceive;
	@MockBean
	private ReservationsApi resApi;
	@MockBean
	private ConnectionFactory cf;

	@Test
	public void basket_not_found() {
		final var basketId = randomUUID();

		final ProcessInstance processInstance = this.checkout.start(basketId, randomAscii(10, 20));
		final Message<MessagePayload<String>> msg = this.checkoutChannelMock.outputMessage();

		assertThat(msg.getHeaders().get(HEADER_CORRELATION), equalTo(basketId));
		assertThat(msg.getHeaders().get(HEADER_KIND), equalTo(BASKET_MESSAGE + "_" + Direction.REQUEST));

		final var payload = msg.getPayload();

		assertThat(payload.get(), equalTo(Basket.Status.ORDERING.name()));

		this.basketReceive.messageReceived(MessagePayload.empty(), basketId, BASKET_MESSAGE + "_" + Direction.RESPONSE);

		assertThat(processInstance).isEnded();
	}
}

package ascelion.micro.checkout;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import ascelion.micro.checkout.api.CheckoutMessageSender;
import ascelion.micro.flow.CheckoutFlow;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.checkout.api.CheckoutChannel.SHIPPING_MESSAGE;
import static ascelion.micro.flow.CheckoutConstants.PROCESS_NAME;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Endpoint("checkouts")
public class CheckoutEndpoint {

	@Autowired
	private CheckoutFlow flow;
	@Autowired
	private ProcessEngine camunda;

	@ApiOperation("Start the checkout process")
	@PostMapping(path = "{basketId}", produces = TEXT_PLAIN_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public String checkout(
	//@formatter:off
	        @ApiParam(value = "The basket id", required = true)
	        @PathVariable("basketId")
	        @NotNull UUID basketId ) {
	//@formatter:on
		return this.flow.start(basketId);
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<Process> getHistory() {
		return this.camunda.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.processDefinitionKey(PROCESS_NAME)
				.orderByProcessInstanceStartTime().asc()
				.list().stream()
				.map(this::toProcess)
				.collect(toList());
	}

	private Process toProcess(HistoricProcessInstance hpi) {
		return Process.builder()
				.id(hpi.getId())
				.state(hpi.getState())
				.activities(toActivities(hpi))
				.build();
	}

	static private final Collection<String> TYPES = asList(new String[] {
			ActivityTypes.TASK,
			ActivityTypes.TASK_SCRIPT,
			ActivityTypes.TASK_SERVICE,
			ActivityTypes.TASK_BUSINESS_RULE,
			ActivityTypes.TASK_MANUAL_TASK,
			ActivityTypes.TASK_USER_TASK,
			ActivityTypes.TASK_SEND_TASK,
			ActivityTypes.TASK_RECEIVE_TASK,
			ActivityTypes.BOUNDARY_ERROR,
			ActivityTypes.BOUNDARY_TIMER,
			ActivityTypes.BOUNDARY_COMPENSATION,
			ActivityTypes.END_EVENT_NONE,
	});

	private List<Activity> toActivities(HistoricProcessInstance hpi) {
		return this.camunda.getHistoryService()
				.createHistoricActivityInstanceQuery()
				.processInstanceId(hpi.getId())
				.finished()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list().stream()
				.filter(hai -> TYPES.contains(hai.getActivityType()))
				.map(this::toActivity)
				.collect(toList());
	}

	private Activity toActivity(HistoricActivityInstance hai) {
		return Activity.builder()
				.id(hai.getActivityId())
				.name(hai.getActivityName())
				.type(hai.getActivityType())
				.build();
	}

	@Autowired
	private CheckoutMessageSender<String> cms;

	@ApiOperation("Complete shipping")
	@PostMapping(path = "{pid}/{status}")
	public void complete(
	//@formatter:off
	        @ApiParam(value = "The process id", required = true)
	        @PathVariable("pid")
	        @NotNull UUID pid,
	        @ApiParam(value = "The shipping status", required = true)
	        @PathVariable("status")
	        @NotNull String  status) {
	//@formatter:on
		this.cms.send("shipping", Direction.RESPONSE, pid, SHIPPING_MESSAGE, MessagePayload.of(status));
	}
}

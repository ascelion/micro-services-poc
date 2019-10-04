package ascelion.micro.payment;

import java.util.UUID;

import ascelion.micro.account.Account;
import ascelion.micro.account.AccountRepo;
import ascelion.micro.card.Card;
import ascelion.micro.payment.api.PaymentMessageSender;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;
import ascelion.micro.shared.message.MessagePayload;
import ascelion.micro.shared.message.MessageSenderAdapter.Direction;

import static ascelion.micro.payment.api.PaymentChannel.PAYMENT_MESSAGE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Endpoint("payments")
public class PaymentEndpoint extends ViewEntityEndpointBase<Payment, PaymentRepo> {
	@Autowired
	private AccountRepo accounts;
	@Autowired
	private PaymentMessageSender<UUID> cms;

	public PaymentEndpoint(PaymentRepo repo) {
		super(repo);
	}

	@PutMapping(path = "{id}", consumes = APPLICATION_FORM_URLENCODED_VALUE)
	public void approve(@PathVariable("id") UUID id, @RequestParam("pin") String pin) {
		final Payment payment = this.repo.getById(id);
		final Card card = payment.getCard();

		if (!card.verify(pin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid pin");
		}

		final Account account = card.getAccount();

		if (!account.debit(payment.getAmount())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "inssuficient funds");
		}

		payment.approve();

		this.repo.save(payment);
		this.accounts.save(account);

		this.cms.send(Direction.RESPONSE, payment.getRequestId(), PAYMENT_MESSAGE, MessagePayload.of(payment.getId()));
	}
}

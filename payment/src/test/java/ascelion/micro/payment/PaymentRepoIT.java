package ascelion.micro.payment;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.account.Account;
import ascelion.micro.account.AccountRepo;
import ascelion.micro.card.Card;
import ascelion.micro.card.CardRepo;
import ascelion.micro.shared.model.IBAN;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
public class PaymentRepoIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private AccountRepo accounts;
	@Autowired
	private CardRepo cards;
	@Autowired
	private PaymentRepo payments;

	@Before
	public void setUp() {
		this.tem.setFlushMode(FlushModeType.AUTO);
	}

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final var a1 = Account.builder()
				.customerId(randomUUID())
				.amount(randomDecimal(1000, 1100))
				.number(IBAN.valueOf(randomAscii(20, 20)))
				.build();
		{
			this.tem.persist(a1);
			this.tem.flush();
			this.tem.detach(a1);

			assertThat(a1.getId(), notNullValue());

			final var a2o = this.accounts.findById(a1.getId());

			assertThat(a2o.isPresent(), is(true));

			final var a2 = a2o.get();

			assertThat(a2, not(sameInstance(a1)));

			assertThat(a2.getId(), notNullValue());
			assertThat(a2.beq(a1), is(true));
		}

		final var c1 = Card.builder()
				.account(a1)
				.number(randomAscii(20, 20))
				.expiration(LocalDate.now().plusYears(2))
				.pin(randomAscii(5, 8))
				.build();
		{
			this.tem.persist(c1);
			this.tem.flush();
			this.tem.detach(c1);

			assertThat(c1.getId(), notNullValue());

			final var c2o = this.cards.findById(c1.getId());

			assertThat(c2o.isPresent(), is(true));

			final var c2 = c2o.get();

			assertThat(c2, not(sameInstance(c1)));

			assertThat(c2.getId(), notNullValue());
			assertThat(c2.beq(c1), is(true));

			final var c3o = this.cards.findByNumber(c1.getNumber());

			assertThat(c3o.isPresent(), is(true));
		}

		final Payment p1 = new Payment(c1, randomDecimal(10, 20), randomUUID());
		{
			this.tem.persist(p1);
			this.tem.flush();
			this.tem.detach(p1);

			assertThat(p1.getId(), notNullValue());

			final var p2o = this.payments.findById(p1.getId());

			assertThat(p2o.isPresent(), is(true));

			final var p2 = p2o.get();

			assertThat(p2, not(sameInstance(p1)));

			assertThat(p2.getId(), notNullValue());
			assertThat(p2.beq(p1), is(true));
		}
	}
}

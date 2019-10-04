package ascelion.micro.payment;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ascelion.micro.card.Card;
import ascelion.micro.shared.model.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends AbstractEntity<Payment> {
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@NotNull
	@Valid
	private Card card;

	@NotNull
	@Column(updatable = false)
	private BigDecimal amount;

	@NotNull
	@Column(updatable = false)
	private UUID requestId;

	private boolean approved;

	Payment(Card card, BigDecimal amount, UUID requestId) {
		this.card = card;
		this.amount = amount;
		this.requestId = requestId;
	}

	void approve() {
		this.approved = true;
	}
}

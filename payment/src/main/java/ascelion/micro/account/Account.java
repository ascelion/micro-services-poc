package ascelion.micro.account;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Account extends AbstractEntity<Account> {
	@NotNull
	@Column(updatable = false)
	private UUID customerId;

	@NotNull
	@Valid
	@Column(updatable = false)
	private IBAN number;

	@NotNull
	@Builder.Default
	private BigDecimal amount = BigDecimal.ZERO;

	public void credit(BigDecimal value) {
		this.amount = this.amount.add(value);
	}

	public boolean debit(BigDecimal value) {
		if (value.compareTo(this.amount) > 0) {
			return false;
		}

		this.amount = this.amount.subtract(value);

		return true;
	}
}

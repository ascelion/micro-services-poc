package ascelion.micro.card;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.account.Account;
import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Card extends AbstractEntity<Card> {
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@NotNull
	@Valid
	private Account account;

	@NotNull
	@Size(min = 12, max = 20)
	@Column(updatable = false, unique = true)
	private String number;

	@NotNull
	private LocalDate expiration;

	@NotNull
	@Size(min = 4, max = 8)
	private String pin;

	public boolean verify(String pin) {
		return this.pin.equals(pin);
	}
}

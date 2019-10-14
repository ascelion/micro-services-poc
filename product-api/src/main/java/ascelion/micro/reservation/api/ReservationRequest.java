package ascelion.micro.reservation.api;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
	@NotNull
	private UUID ownerId;
	@NotNull
	private UUID productId;
	@NotNull
	private BigDecimal quantity;
}

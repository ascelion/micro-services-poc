package ascelion.micro.reservation.api;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationRequest {
	@NotNull
	private final UUID ownerId;
	@NotNull
	private final UUID productId;
	@NotNull
	private final BigDecimal quantity;
}

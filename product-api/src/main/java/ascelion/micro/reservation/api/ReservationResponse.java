package ascelion.micro.reservation.api;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationResponse {
	@NotNull
	private final BigDecimal quantity;
	@NotNull
	private final BigDecimal price;
}

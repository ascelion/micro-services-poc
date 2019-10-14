package ascelion.micro.reservation.api;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
	@NotNull
	private BigDecimal quantity;
	@NotNull
	private BigDecimal price;
}

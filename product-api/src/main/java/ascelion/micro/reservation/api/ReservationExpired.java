package ascelion.micro.reservation.api;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationExpired implements Serializable {
	public static final String QUEUE_NAME = "RESERVATION_EXPIRED";

	@NotNull
	private UUID productId;
	@NotNull
	private UUID ownerId;
}

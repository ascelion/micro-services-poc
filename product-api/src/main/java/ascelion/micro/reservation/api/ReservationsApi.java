package ascelion.micro.reservation.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product", path = "reservations")
public interface ReservationsApi {
	enum Operation {
		LOCK,
		COMMIT,
		DISCARD,
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ReservationResponse[] reserve(@RequestBody @NotNull @Valid ReservationRequest... reservations);

	@PostMapping(path = "{op}", consumes = APPLICATION_JSON_VALUE)
	ReservationResponse[] update(@PathVariable("op") Operation op, @RequestBody @NotNull @Valid ReservationRequest... reservations);
}

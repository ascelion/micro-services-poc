package ascelion.micro.reservations;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "products", path = "reservations")
public interface ReservationsApi {
	enum Finalize {
		COMMIT,
		DISCARD,
	}

	@PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ReservationRequest[] reserve(@RequestBody @NotNull @Valid ReservationRequest... reservations);

	@DeleteMapping(path = "{op}", consumes = APPLICATION_JSON_VALUE)
	void finalize(@PathVariable("op") Finalize op, @RequestBody @NotNull @Valid ReservationRequest... reservations);
}

package ascelion.micro.reservation;

import ascelion.micro.product.ProductRepo;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationsApi.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ProductRepo prdRepo;
	private final ReservationRepo resRepo;

	@Transactional
	public Reservation update(Operation op, final Reservation res) {
		final var product = res.getProduct();
		final var quantity = product.getStock().subtract(res.getQuantity());

		switch (op) {
		case LOCK:
			res.setLocked(true);
			product.setStock(quantity);

			this.prdRepo.save(product);

			break;

		case COMMIT:
			product.setStock(quantity);

			this.prdRepo.save(product);

			break;

		case DISCARD:
			this.resRepo.delete(res);

			break;
		}

		return res;
	}
}

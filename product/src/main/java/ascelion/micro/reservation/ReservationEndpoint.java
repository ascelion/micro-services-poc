package ascelion.micro.reservation;

import java.math.BigDecimal;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.product.ProductRepo;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationRequest;
import ascelion.micro.reservation.api.ReservationResponse;
import ascelion.micro.reservation.api.ReservationsApi;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;

import static java.util.Arrays.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Endpoint("reservations")
@BBMap(from = Reservation.class, to = ReservationResponse.class, bidi = false, fields = {
		@BBField(from = "product.price", to = "price")
})
public class ReservationEndpoint extends ViewEntityEndpointBase<Reservation, ReservationRepo> implements ReservationsApi {

	private final ProductRepo prdRepo;

	@Autowired
	private BeanToBeanMapper bbm;
	@Autowired
	private ReservationService rs;

	public ReservationEndpoint(ReservationRepo repo, ProductRepo prdRepo) {
		super(repo);

		this.prdRepo = prdRepo;
	}

	@Override
	@Transactional
	public ReservationResponse[] reserve(ReservationRequest... reservations) {
		return stream(reservations)
				.map(this::reserve)
				.toArray(ReservationResponse[]::new);
	}

	@Override
	@Transactional
	public ReservationResponse[] update(Operation op, ReservationRequest... requests) {
		final var reservations = new Reservation[requests.length];

		for (int k = 0; k < requests.length; k++) {
			final var req = requests[k];
			final var res = this.repo.getByProductIdAndOwnerId(req.getProductId(), req.getOwnerId());

			reservations[k] = this.rs.update(op, res);
		}

		return this.bbm.createArray(ReservationResponse.class, reservations);
	}

	private ReservationResponse reserve(ReservationRequest req) {
		final var product = this.prdRepo.getById(req.getProductId());
		final var avail = this.prdRepo.stockAvailability(product);
		var quantity = req.getQuantity();

		if (quantity.compareTo(avail) > 0) {
			quantity = avail;
		}

		if (quantity.compareTo(BigDecimal.ZERO) > 0) {
			var res = this.repo.findByProductIdAndOwnerId(req.getProductId(), req.getOwnerId())
					.orElse(null);

			if (res == null) {
				res = Reservation.builder()
						.product(product)
						.ownerId(req.getOwnerId())
						.build();
			}

			res.setQuantity(quantity);

			this.repo.save(res);
		}

		return new ReservationResponse(quantity, product.getPrice());
	}

}

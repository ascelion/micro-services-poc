package ascelion.micro.reservation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.product.ProductRepo;
import ascelion.micro.product.api.Product;
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
@BBMap(from = Reservation.class, to = ReservationRequest.class, bidi = false, fields = {
		@BBField(from = "product.id", to = "productId"),
})
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
	public ReservationResponse[] update(Operation op, ReservationRequest... reservations) {
		final List<Reservation> result = new ArrayList<>();

		for (final ReservationRequest req : reservations) {
			final Reservation r = this.repo.getByProductIdAndOwnerId(req.getProductId(), req.getOwnerId());

			this.rs.update(op, r);

			result.add(r);
		}

		return this.bbm.createArray(ReservationResponse.class, result);
	}

	private ReservationResponse reserve(ReservationRequest req) {
		final Product p = this.prdRepo.getById(req.getProductId());
		final BigDecimal a = this.prdRepo.stockAvailability(p);
		BigDecimal q = req.getQuantity();

		if (q.compareTo(a) > 0) {
			q = a;
		}

		if (q.compareTo(BigDecimal.ZERO) > 0) {
			Reservation s = this.repo.findByProductIdAndOwnerId(req.getProductId(), req.getOwnerId())
					.orElse(null);

			if (s == null) {
				s = Reservation.builder()
						.product(p)
						.ownerId(req.getOwnerId())
						.quantity(q)
						.build();
			}

			this.repo.save(s);
		}

		return new ReservationResponse(q, p.getPrice());
	}

}

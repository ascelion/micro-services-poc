package ascelion.micro.reservations;

import java.math.BigDecimal;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.products.Product;
import ascelion.micro.products.ProductsRepository;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;

import static java.util.Arrays.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Endpoint("reservations")
@BBMap(from = Reservation.class, to = ReservationRequest.class, bidi = false, fields = {
		@BBField(from = "product.id", to = "productId"),
})
public class ReservationsController extends ViewEntityEndpointBase<Reservation, ReservationsRepository> implements ReservationsApi {

	private final ProductsRepository prdRepo;

	@Autowired
	private BeanToBeanMapper bbm;

	public ReservationsController(ReservationsRepository repo, ProductsRepository prdRepo) {
		super(repo);
		this.prdRepo = prdRepo;
	}

	@Override
	@Transactional
	public ReservationRequest[] reserve(ReservationRequest... reservations) {
		return stream(reservations)
				.map(this::reserve)
				.toArray(ReservationRequest[]::new);
	}

	@Override
	@Transactional
	public void finalize(Finalize op, ReservationRequest... reservations) {
		for (final ReservationRequest req : reservations) {
			final Reservation s = this.repo.getByProductIdAndOwnerId(req.getProductId(), req.getOwnerId());
			final Product p = s.getProduct();

			switch (op) {
			case COMMIT:
				final BigDecimal q = p.getStock().subtract(s.getQuantity());

				p.setStock(q);

				this.prdRepo.save(p);
			case DISCARD:
				this.repo.delete(s);
			}
		}
	}

	private ReservationRequest reserve(ReservationRequest req) {
		final Product p = this.prdRepo.getById(req.getProductId());
		final BigDecimal a = this.prdRepo.stockAvailability(p);
		BigDecimal q = req.getQuantity();

		if (q.compareTo(a) > 0) {
			req.setQuantity(q = a);
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

			return this.bbm.create(ReservationRequest.class, req, s);
		}

		return req;
	}

}

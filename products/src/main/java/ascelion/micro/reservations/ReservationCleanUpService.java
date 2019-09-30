package ascelion.micro.reservations;

import java.time.LocalDateTime;
import java.util.List;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@BBMap(from = Reservation.class, to = ReservationExpired.class, bidi = false, fields = {
		@BBField(from = "product.id", to = "productId"),
})
public class ReservationCleanUpService {

	private final ReservationsRepository resRepo;

	@Value("${reservation.availability:60}")
	private int availability;

	@Autowired
	private BeanToBeanMapper bbm;

	@Autowired
	private AmqpTemplate amqp;

	@Scheduled(fixedDelay = 60000)
	@Transactional
	public void run() {
		final LocalDateTime exp = LocalDateTime.now().minusMinutes(this.availability);
		final List<Reservation> old = this.resRepo.findOlderThan(exp);

		if (old.size() > 0) {
			final ReservationExpired[] payload = this.bbm.createArray(ReservationExpired.class, old);

			this.amqp.convertAndSend(ReservationExpired.QUEUE_NAME, payload);

			this.resRepo.deleteAll(old);
		}
	}
}

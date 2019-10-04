package ascelion.micro.reservation;

import java.time.LocalDateTime;
import java.util.List;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.reservation.api.ReservationExpired;

import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
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
	static private final Logger L = loggerForThisClass();

	private final ReservationRepo repo;

	@Value("${reservation.availability:60}")
	private int availability;

	@Autowired
	private BeanToBeanMapper bbm;

	@Autowired
	private AmqpTemplate amqp;
	@Autowired
	private FanoutExchange reservationExchange;

	@Scheduled(fixedDelay = 60000)
	@Transactional
	public void run() {
		final LocalDateTime exp = LocalDateTime.now().minusMinutes(this.availability);
		final List<Reservation> old = this.repo.findOlderThan(exp);

		if (old.size() > 0) {
			L.info("Found {} expired reservations", old.size());

			final ReservationExpired[] payload = this.bbm.createArray(ReservationExpired.class, old);

			this.amqp.convertAndSend(this.reservationExchange.getName(), "", payload);
			this.repo.deleteAll(old);
		}
	}
}

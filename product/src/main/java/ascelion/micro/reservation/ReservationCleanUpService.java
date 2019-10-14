package ascelion.micro.reservation;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableScheduling
@BBMap(from = Reservation.class, to = ReservationExpired.class, bidi = false, fields = {
		@BBField(from = "product.id", to = "productId"),
})
@EnableConfigurationProperties(ReservationProperties.class)
public class ReservationCleanUpService {
	static private final Logger L = loggerForThisClass();

	private final ReservationRepo repo;

	@Autowired
	private ReservationProperties config;

	@Autowired
	private TaskScheduler ts;

	@Autowired
	private BeanToBeanMapper bbm;

	@Autowired
	private AmqpTemplate amqp;
	@Autowired
	private FanoutExchange reservationExchange;

	@Transactional
	public void run() {
		L.info("Running reservation cleaner");

		final var exp = LocalDateTime.now().minus(this.config.getAvailability());
		final var old = this.repo.findOlderThan(exp);

		if (old.size() > 0) {
			L.info("Found {} expired reservations", old.size());

			final var payload = this.bbm.createArray(ReservationExpired.class, old);

			this.amqp.convertAndSend(this.reservationExchange.getName(), "", payload);
			this.repo.deleteAll(old);
		}
	}

	@PostConstruct
	private void postConstruct() {
		this.ts.scheduleAtFixedRate(this::run, this.config.getCheckInterval());
	}
}

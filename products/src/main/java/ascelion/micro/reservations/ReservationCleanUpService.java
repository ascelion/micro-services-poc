package ascelion.micro.reservations;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationCleanUpService {

	private final ReservationsRepository resRepo;

	@Value("${reservation.availability:60}")
	private int availability;

	@Scheduled(fixedDelay = 60000)
	@Transactional
	public void run() {
		this.resRepo.deleteAllOlderThan(LocalDateTime.now().minusMinutes(this.availability));
	}
}

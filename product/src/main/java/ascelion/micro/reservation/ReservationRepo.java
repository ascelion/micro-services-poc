package ascelion.micro.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.reservation.api.Reservation;
import ascelion.micro.shared.model.EntityRepo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepo extends EntityRepo<Reservation> {

	@Query("SELECT r FROM Reservation r WHERE r.updatedAt < :tm AND r.locked <> TRUE")
	List<Reservation> findOlderThan(@Param("tm") LocalDateTime tm);

	default Reservation getByProductIdAndOwnerId(UUID productId, UUID ownerId) {
		return findByProductIdAndOwnerId(productId, ownerId)
				.orElseThrow(() -> new EntityNotFoundException("no such reservation: " + productId));
	}

	Optional<Reservation> findByProductIdAndOwnerId(UUID productId, UUID ownerId);
}

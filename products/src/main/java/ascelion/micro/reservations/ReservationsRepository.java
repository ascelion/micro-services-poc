package ascelion.micro.reservations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationsRepository extends EntityRepository<Reservation> {

	@Query("SELECT r FROM Reservation r WHERE r.createdAt < :tm")
	List<Reservation> findOlderThan(@Param("tm") LocalDateTime tm);

	default Reservation getByProductIdAndOwnerId(UUID productId, UUID ownerId) {
		return findByProductIdAndOwnerId(productId, ownerId)
				.orElseThrow(() -> new EntityNotFoundException("no such reservation: " + productId));
	}

	Optional<Reservation> findByProductIdAndOwnerId(UUID productId, UUID ownerId);
}

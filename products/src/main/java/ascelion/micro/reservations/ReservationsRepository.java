package ascelion.micro.reservations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationsRepository extends EntityRepository<Reservation> {

	@Query("DELETE FROM Reservation r WHERE r.createdAt < :tm")
	@Modifying
	void deleteAllOlderThan(@Param("tm") LocalDateTime tm);

	Optional<Reservation> findByProductIdAndOwnerId(UUID productId, UUID ownerId);
}

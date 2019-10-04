package ascelion.micro.card;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.shared.model.EntityRepo;

public interface CardRepo extends EntityRepo<Card> {
	default Card getByNumber(String number) {
		return findByNumber(number)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find card with number " + number));
	}

	Optional<Card> findByNumber(String number);
}

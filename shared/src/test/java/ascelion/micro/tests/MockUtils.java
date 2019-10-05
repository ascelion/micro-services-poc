package ascelion.micro.tests;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepo;

import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;
import static org.springframework.security.util.FieldUtils.setProtectedFieldValue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MockUtils {

	static public <T extends AbstractEntity<T>> void mockRepository(BeanToBeanMapper bbm, EntityRepo<T> repo, Map<UUID, T> entities, Supplier<T> sup) {
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) resolveTypeArguments(repo.getClass(), EntityRepo.class)[0];

		for (int k = 0; k < 10; k++) {
			final var now = LocalDateTime.now();
			final T ent = sup.get();

			setProtectedFieldValue("id", ent, randomUUID());
			setProtectedFieldValue("createdAt", ent, now);
			setProtectedFieldValue("updatedAt", ent, now);

			entities.put(ent.getId(), ent);
		}
		when(repo.findAll(any(Sort.class)))
				.then(ivc -> {
					return bbm.createList(type, entities.values());
				});
		when(repo.findAll(any(PageRequest.class)))
				.then(ivc -> {
					return bbm.createList(type, entities.values());
				});
		when(repo.findById(any()))
				.then(ivc -> {
					return ofNullable(entities.get(ivc.getArgument(0)));
				});
		when(repo.save(any()))
				.then(ivc -> {
					final var now = LocalDateTime.now();
					final T ent = ivc.getArgument(0);

					if (ent.getId() == null) {
						setProtectedFieldValue("id", ent, randomUUID());
						setProtectedFieldValue("createdAt", ent, now);

						entities.put(ent.getId(), ent);
					} else if (!entities.containsKey(ent.getId())) {
						throw new EntityNotFoundException("Cannot find entity with id " + ent.getId());
					}

					setProtectedFieldValue("updatedAt", ent, now);

					return ent;
				});
	}
}

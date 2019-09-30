package ascelion.micro.tests;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepository;

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

	static public <T extends AbstractEntity<T>> void mockRepository(BeanToBeanMapper bbm, EntityRepository<T> repo, Map<UUID, T> entities, Supplier<T> sup) {
		final Class<T> type = (Class<T>) resolveTypeArguments(repo.getClass(), EntityRepository.class)[0];

		for (int k = 0; k < 10; k++) {
			final LocalDateTime now = LocalDateTime.now();
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
					final T old = ivc.getArgument(0);
					final LocalDateTime now = LocalDateTime.now();
					final T ent;

					if (old.getId() == null) {
						ent = bbm.create(type, old);

						setProtectedFieldValue("id", ent, randomUUID());
						setProtectedFieldValue("createdAt", ent, now);

						entities.put(ent.getId(), ent);
					} else {
						ent = entities.get(old.getId());

						if (ent == null) {
							throw new EntityNotFoundException("Cannot find entity with id " + old.getId());
						} else {
							bbm.copyWithNulls(ent, old);
						}
					}

					setProtectedFieldValue("updatedAt", ent, now);

					return ent;
				});
	}
}

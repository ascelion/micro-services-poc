package ascelion.micro.shared.model;

import static java.util.UUID.randomUUID;

public class EntityUtil {

	static public void populate(AbstractEntity<?>... entities) {
		for (final var ent : entities) {
			ent.id = randomUUID();
			ent.init();
		}
	}

}

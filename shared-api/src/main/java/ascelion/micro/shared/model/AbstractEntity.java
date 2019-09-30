package ascelion.micro.shared.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.validation.OnUpdate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;

@MappedSuperclass
@Getter
@Setter // this is temporary
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity<E extends AbstractEntity<E>> {
	@Id
	@GeneratedValue
	@Column(nullable = false, insertable = false, updatable = false, unique = true)
	@NotNull(groups = OnUpdate.class)
	private UUID id;
	@Column(nullable = false, updatable = false)
	@NotNull
	private LocalDateTime createdAt;
	@Column(nullable = false, updatable = true)
	@NotNull
	private LocalDateTime updatedAt;

	@PrePersist
	private void init() {
		this.createdAt = this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	private void touch() {
		this.updatedAt = LocalDateTime.now();
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final AbstractEntity<?> that = (AbstractEntity<?>) obj;

		return Objects.equals(this.id, that.id);
	}

	/**
	 * Business equality.
	 */
	public boolean beq(E that) {
		Class<?> t = getClass();

		while (t.getSuperclass() != AbstractEntity.class) {
			t = t.getSuperclass();
		}

		return EqualsBuilder.reflectionEquals(this, that, false, t);
	}

	static protected <T extends AbstractEntity<T>> boolean beq(Collection<T> c1, Collection<T> c2) {
		if (c1 == c2) {
			return true;
		}
		if (c1 == null || c2 == null) {
			return false;
		}
		if (c1.size() != c2.size()) {
			return false;
		}

		for (Iterator<T> i1 = c1.iterator(), i2 = c2.iterator(); i1.hasNext();) {
			if (!i1.next().beq(i2.next())) {
				return false;
			}
		}

		return true;
	}
}

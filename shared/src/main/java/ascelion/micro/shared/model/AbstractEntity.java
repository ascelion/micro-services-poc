package ascelion.micro.shared.model;

import java.time.LocalDateTime;
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

@MappedSuperclass
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, insertable = false, updatable = false, unique = true)
	@NotNull(groups = OnUpdate.class)
	private UUID id;
	@Column(name = "CREATED_AT", nullable = false, updatable = false)
	@NotNull
	private LocalDateTime createdAt;
	@Column(name = "UPDATED_AT", nullable = false, updatable = true)
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

		final AbstractEntity that = (AbstractEntity) obj;

		return Objects.equals(this.id, that.id);
	}
}

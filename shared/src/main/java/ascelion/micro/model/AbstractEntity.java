package ascelion.micro.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import ascelion.micro.validation.OnUpdate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EqualsAndHashCode(of = "id") // Prefer identity equality
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false, updatable = false, unique = true)
	@NotNull(groups = OnUpdate.class)
	private Long id;
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
}

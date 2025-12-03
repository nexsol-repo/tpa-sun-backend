package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)", nullable = false)
	private EntityStatus status = EntityStatus.ACTIVE;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public void active() {
		this.status = EntityStatus.ACTIVE;
	}

	public boolean isActive() {
		return this.status == EntityStatus.ACTIVE;
	}

	public void delete() {
		this.status = EntityStatus.DELETED;
	}

	public boolean isDeleted() {
		return this.status == EntityStatus.DELETED;
	}

	protected void setId(Long id) {
		this.id = id;
	}

}

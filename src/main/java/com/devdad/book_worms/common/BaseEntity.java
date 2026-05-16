
package com.devdad.book_worms.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder // Need super version due to this class being inherited our sub classes wouldnt
							// be able to access the class properties
@AllArgsConstructor
@NoArgsConstructor
// Inherit these fields and map them to the database tables of my child
// classes, but do not create a separate table for this parent class itself.
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

	@Id
	@GeneratedValue
	private Integer id;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(insertable = false)
	private LocalDateTime lastModifiedDate;

	// Reference to the user id
	@CreatedBy
	@Column(nullable = false, updatable = false)
	private Integer createdBy;

	@LastModifiedBy
	@Column(insertable = false)
	private Integer lastModifiedBy;

}

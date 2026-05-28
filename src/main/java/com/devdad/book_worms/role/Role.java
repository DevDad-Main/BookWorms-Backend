
package com.devdad.book_worms.role;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.devdad.book_worms.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// @Entity
// @Table(name = "role")
// @EntityListeners(AuditingEntityListener.class)
public class Role {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(unique = true)
	private String name;

	@ManyToMany(mappedBy = "roles")
	@JsonIgnore // Ignore the serializtion of this field, stops circular dependency error.
	private List<User> users;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDate createdDate;

	@LastModifiedDate
	@Column(insertable = false)
	private LocalDate lastModifiedDate;
}

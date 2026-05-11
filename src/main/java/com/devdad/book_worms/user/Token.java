package com.devdad.book_worms.user;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

	@Id
	@GeneratedValue
	private Integer id;

	private String token;
	private LocalDate createdAt;
	private LocalDate expiresAt;
	private LocalDate validatedAt;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
}

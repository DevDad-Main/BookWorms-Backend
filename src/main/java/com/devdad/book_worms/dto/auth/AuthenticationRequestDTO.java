package com.devdad.book_worms.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {

	@NotEmpty(message = "Email is mandatory.")
	@NotBlank(message = "Email is mandatory.")
	@Email(message = "Email is not a valid email format.")
	private String email;

	@NotEmpty(message = "Password is mandatory.")
	@NotBlank(message = "Password is mandatory.")
	@Size(min = 8, message = "Password should be a min of 8 characters long.")
	private String password;
}

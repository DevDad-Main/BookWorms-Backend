package com.devdad.book_worms.dto.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequestDTO(
		Integer id,

		@NotNull(message = "Title should not be empty")
		@NotEmpty(message = "Title should not be empty")
		String title,

		@NotNull(message = "Author name should not be empty")
		@NotEmpty(message = "Author name should not be empty")
		String authorName,

		@NotNull(message = "ISBN should not be empty")
		@NotEmpty(message = "ISBN should not be empty")
		String isbn,

		@NotNull(message = "Synopsis should not be empty")
		@NotEmpty(message = "Synopsis should not be empty")
		String synopsis,

		boolean shareable
		) {
}

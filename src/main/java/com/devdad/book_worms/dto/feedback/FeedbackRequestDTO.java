
package com.devdad.book_worms.dto.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeedbackRequestDTO(
		@Positive(message = "Note value must be a positive number between 0 and 5.") @Min(value = 0, message = "Note must be within the minimum value of 0.") @Max(value = 5, message = "Note must be within the maximum value of 5.") Double note,

		@NotNull(message = "Comment field cannot be empty") @NotEmpty(message = "Comment field cannot be empty") @NotBlank(message = "Comment field cannot be empty") String comment,

		@NotNull(message = "Book ID cannot be null or empty.") Integer bookId) {
}

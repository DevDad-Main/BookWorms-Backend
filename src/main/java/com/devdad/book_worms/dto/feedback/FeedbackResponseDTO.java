package com.devdad.book_worms.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDTO {
	private Double note;
	private String comment;
	// Owners feedback , mark it differently on the FE to differentiate.
	private boolean ownFeedback;
}

package com.devdad.book_worms.mapper;

import java.util.Objects;

import com.devdad.book_worms.dto.feedback.FeedbackRequestDTO;
import com.devdad.book_worms.dto.feedback.FeedbackResponseDTO;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.feedback.Feedback;

public class FeedbackMapper {

	public static Feedback toFeedback(FeedbackRequestDTO request) {
		return Feedback.builder()
				.note(request.note())
				.book(Book.builder().id(request.bookId()).build())
				.build();
	}

	public static FeedbackResponseDTO toFeedbackResponseDTO(Feedback feedback, Integer userId) {
		return FeedbackResponseDTO.builder()
				.note(feedback.getNote())
				.comment(feedback.getComment())
				.ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
				.build();
	}
}

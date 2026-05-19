package com.devdad.book_worms.service;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devdad.book_worms.common.PageResponse;
import com.devdad.book_worms.dto.feedback.FeedbackRequestDTO;
import com.devdad.book_worms.dto.feedback.FeedbackResponseDTO;
import com.devdad.book_worms.exception.OperationNotPermittedException;
import com.devdad.book_worms.mapper.FeedbackMapper;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.feedback.Feedback;
import com.devdad.book_worms.model.user.User;
import com.devdad.book_worms.respository.BookRepository;
import com.devdad.book_worms.respository.FeedbackRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

	private final FeedbackRepository feedbackRepository;
	private final BookRepository bookRepository;

	public Integer saveFeedback(FeedbackRequestDTO request, Authentication currentUser) {
		Book book = bookRepository.findById(request.bookId())
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));

		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"You cannot give feedback to an archived or not shareable book");
		}

		User user = (User) currentUser.getPrincipal();

		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot give a feedback to your own book.");
		}

		Feedback feedback = FeedbackMapper.toFeedback(request);

		return feedbackRepository.save(feedback).getId();
	}

	public PageResponse<FeedbackResponseDTO> findAllFeedbacksByBook(Integer bookId, int page, int size,
			Authentication currentUser) {
		PageRequest pageable = PageRequest.of(page, size);
		User user = (User) currentUser.getPrincipal();
		Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
		List<FeedbackResponseDTO> feedbackResponses = feedbacks.stream()
				.map(f -> FeedbackMapper.toFeedbackResponseDTO(f, user.getId())).toList();

		return new PageResponse<>(
				feedbackResponses,
				feedbacks.getNumber(),
				feedbacks.getSize(),
				feedbacks.getTotalElements(),
				feedbacks.getTotalPages(),
				feedbacks.isFirst(),
				feedbacks.isLast()
				);
	}

}

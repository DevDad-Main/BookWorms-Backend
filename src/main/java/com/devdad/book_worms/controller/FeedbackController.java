package com.devdad.book_worms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devdad.book_worms.common.PageResponse;
import com.devdad.book_worms.dto.feedback.FeedbackRequestDTO;
import com.devdad.book_worms.dto.feedback.FeedbackResponseDTO;
import com.devdad.book_worms.service.FeedbackService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

	private final FeedbackService feedbackService;

	@PostMapping
	public ResponseEntity<Integer> saveFeedback(
			@Valid @RequestBody FeedbackRequestDTO request,
			Authentication currentUser) {
		return ResponseEntity.ok(feedbackService.saveFeedback(request, currentUser));
	}

	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponseDTO>> findAllFeedbacksByBook(
			@PathVariable("book-id") Integer bookId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "page", defaultValue = "0", required = false) int size,
			Authentication currentUser) {
		return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, currentUser));
	}
}

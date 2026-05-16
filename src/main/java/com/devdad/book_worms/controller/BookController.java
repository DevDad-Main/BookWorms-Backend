package com.devdad.book_worms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.service.BookService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

	private final BookService bookService;

	@PostMapping
	public ResponseEntity<Integer> saveBook(
			@RequestBody @Valid BookRequestDTO request,
			Authentication currentUser // Returns the currently connected/logged in user.
	) {
		return ResponseEntity.ok(bookService.save(request, currentUser));
	}

	@GetMapping("{book-id}")
	public ResponseEntity<BookResponseDTO> findBookById(
			@PathVariable("book-id") Integer bookId
			)
	{
		return ResponseEntity.ok(bookService.findBookById(bookId));
	}
}

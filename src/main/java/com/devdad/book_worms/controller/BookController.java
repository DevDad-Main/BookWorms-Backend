package com.devdad.book_worms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devdad.book_worms.common.PageResponse;
import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.dto.book.BorrowedBookResponseDTO;
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
			@PathVariable("book-id") Integer bookId) {
		return ResponseEntity.ok(bookService.findBookById(bookId));
	}

	@GetMapping
	public ResponseEntity<PageResponse<BookResponseDTO>> findAllBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication currentUser) {
		return ResponseEntity.ok(bookService.findAllBooks(page, size, currentUser));
	}

	@GetMapping("/owner")
	public ResponseEntity<PageResponse<BookResponseDTO>> findAllBooksByOwner(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication currentUser) {

		return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, currentUser));
	}

	@GetMapping("/borrowed")
	public ResponseEntity<PageResponse<BorrowedBookResponseDTO>> findAllBorrowedBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication currentUser) {

		return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, currentUser));
	}

	@GetMapping("/returned")
	public ResponseEntity<PageResponse<BorrowedBookResponseDTO>> findAllReturnedBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication currentUser) {

		return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, currentUser));
	}

	@PatchMapping("/shareable/{book-id}")
	public ResponseEntity<Integer> updateShareableStatus(
			@PathVariable("book-id") Integer bookId,
			Authentication currentUser) {
		return ResponseEntity.ok(bookService.updateShareableStatus(bookId, currentUser));
	}

	@PatchMapping("/archived/{book-id}")
	public ResponseEntity<Integer> updateArchivedStatus(
			@PathVariable("book-id") Integer bookId,
			Authentication currentUser) {
		return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, currentUser));
	}

	@PostMapping("/borrowed/{book-id}")
	public ResponseEntity<Integer> borrowBook(
			@PathVariable("book-id") Integer bookId,
			Authentication currentUser) {

		return ResponseEntity.ok(bookService.borrowBook(bookId, currentUser));
	}

	@PatchMapping("/borrow/return/{book-id}")
	public ResponseEntity<Integer> returnBorrowedBook(
			@PathVariable("book-id") Integer bookId,
			Authentication currentUser) {
		return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, currentUser));
	}
}

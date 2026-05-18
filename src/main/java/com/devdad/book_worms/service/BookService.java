package com.devdad.book_worms.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devdad.book_worms.common.PageResponse;
import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.dto.book.BorrowedBookResponseDTO;
import com.devdad.book_worms.exception.OperationNotPermittedException;
import com.devdad.book_worms.mapper.BookMapper;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.book.BookSpecification;
import com.devdad.book_worms.model.history.BookTransactionHistory;
import com.devdad.book_worms.model.user.User;
import com.devdad.book_worms.respository.BookRepository;
import com.devdad.book_worms.respository.BookTransactionHistoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
	private final FileStorageService fileStorageService;

	public Integer save(BookRequestDTO request, Authentication currentUser) {
		User user = (User) currentUser.getPrincipal();
		Book book = BookMapper.toBook(request);
		book.setOwner(user);

		return bookRepository.save(book).getId();
	}

	public BookResponseDTO findBookById(Integer bookId) {
		return bookRepository.findById(bookId)
				.map(BookMapper::toDTOResponse)
				.orElseThrow(() -> new EntityNotFoundException("No book found with the ID::" + bookId));
	}

	public PageResponse<BookResponseDTO> findAllBooks(int page, int size, Authentication currentUser) {
		User user = (User) currentUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
		List<BookResponseDTO> bookReponse = books.stream()
				.map(BookMapper::toDTOResponse)
				.toList();

		return new PageResponse<>(
				bookReponse,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast());
	}

	public PageResponse<BookResponseDTO> findAllBooksByOwner(int page, int size, Authentication currentUser) {
		User user = (User) currentUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);

		List<BookResponseDTO> bookReponse = books.stream()
				.map(BookMapper::toDTOResponse)
				.toList();

		return new PageResponse<>(
				bookReponse,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast());
	}

	public PageResponse<BorrowedBookResponseDTO> findAllBorrowedBooks(int page, int size, Authentication currentUser) {
		User user = (User) currentUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,
				user.getId());
		List<BorrowedBookResponseDTO> bookResponse = allBorrowedBooks.stream().map(BookMapper::toBorrowedBookResponseDTO)
				.toList();

		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast());
	}

	public PageResponse<BorrowedBookResponseDTO> findAllReturnedBooks(int page, int size, Authentication currentUser) {
		User user = (User) currentUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,
				user.getId());
		List<BorrowedBookResponseDTO> bookResponse = allBorrowedBooks.stream().map(BookMapper::toBorrowedBookResponseDTO)
				.toList();

		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast());
	}

	public Integer updateShareableStatus(Integer bookId, Authentication currentUser) {

		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
		User user = (User) currentUser.getPrincipal();

		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You are not allowed to update books shareable status.");
		}

		book.setShareable(!book.isShareable());
		bookRepository.save(book);

		return bookId;
	}

	public Integer updateArchivedStatus(Integer bookId, Authentication currentUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		User user = (User) currentUser.getPrincipal();

		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You are not allowed to update books archived status.");
		}

		book.setArchived(!book.isArchived());
		bookRepository.save(book);

		return bookId;
	}

	public Integer borrowBook(Integer bookId, Authentication currentUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"Requested book cannot be borrowed since it is archived or not shareable.");
		}

		User user = (User) currentUser.getPrincipal();

		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You are not allowed to borrow your own book.");
		}

		final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());

		if (isAlreadyBorrowed) {
			throw new OperationNotPermittedException("Requested book is already borrowed.");
		}

		BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
				.user(user)
				.book(book)
				.returned(false)
				.returnApproved(false)
				.build();

		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public Integer returnBorrowedBook(Integer bookId, Authentication currentUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"Requested book cannot be borrowed since it is archived or not shareable.");
		}

		User user = (User) currentUser.getPrincipal();

		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You are not allowed to borrow or return your own book.");
		}

		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
				.findByBookIdAndUserId(bookId, user.getId())
				.orElseThrow(() -> new OperationNotPermittedException("You haven't borrowed this book."));

		bookTransactionHistory.setReturned(true);

		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public Integer approveReturnBorrowedBook(Integer bookId, Authentication currentUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"Requested book cannot be borrowed since it is archived or not shareable.");
		}

		User user = (User) currentUser.getPrincipal();

		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You are not allowed to borrow or return your own book.");
		}

		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
				.findByBookIdAndOwnerId(bookId, user.getId())
				.orElseThrow(() -> new OperationNotPermittedException(
						"The book hasn't been returned yet, you cannot approve the returnal."));

		bookTransactionHistory.setReturnApproved(true);

		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public ResponseEntity<?> uploadBookCoverPhoto(MultipartFile file, Integer bookId) throws IOException {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty.");
		}

		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		book.setBookCover(file.getBytes());
		bookRepository.save(book);

		return ResponseEntity.ok("Photo uploaded Successfully.");
	}

	public void uploadBookCoverPhotoLocally(MultipartFile file, Authentication currentUser, Integer bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		User user = (User) currentUser.getPrincipal();
		var bookCover = fileStorageService.saveFile(file,  user.getId());

		// Need to set back to String instead of byte array
		// book.setBookCover(bookCover);
		bookRepository.save(book);

	}
}

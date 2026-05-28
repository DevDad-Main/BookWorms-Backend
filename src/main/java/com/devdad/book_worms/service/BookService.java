package com.devdad.book_worms.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
import com.devdad.book_worms.security.SecurityUtils;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.book.BookSpecification;
import com.devdad.book_worms.model.history.BookTransactionHistory;
import com.devdad.book_worms.respository.BookRepository;
import com.devdad.book_worms.respository.BookTransactionHistoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

	private final BookRepository bookRepository;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
	private final FileStorageService fileStorageService;

	public Integer save(BookRequestDTO request, Authentication currentUser) {
		log.info("Incoming saveBook() Data:: {}", request);

		Book book = BookMapper.toBook(request, SecurityUtils.getUserId(currentUser));

		return bookRepository.save(book).getId();
	}

	public BookResponseDTO findBookById(Integer bookId) {
		return bookRepository.findById(bookId)
				.map(BookMapper::toDTOResponse)
				.orElseThrow(() -> new EntityNotFoundException("No book found with the ID::" + bookId));
	}

	public PageResponse<BookResponseDTO> findAllBooks(int page, int size, Authentication currentUser) {
		log.info("Incoming findAllBooks() Data:: {} {} {}", page, size, currentUser);


		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, SecurityUtils.getUserId(currentUser));

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
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(SecurityUtils.getUserId(currentUser)), pageable);

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
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,
				SecurityUtils.getUserId(currentUser));
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
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,
				SecurityUtils.getUserId(currentUser));
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
		// User user = (User) currentUser.getPrincipal();

		// NOTE: Use the SecurityUtils.getUserId(currentUser) as that will return us the keycloak users
		// ID.
		if (!Objects.equals(book.getCreatedBy(), SecurityUtils.getUserId(currentUser))) {
			throw new OperationNotPermittedException("You are not allowed to update books shareable status.");
		}

		book.setShareable(!book.isShareable());
		bookRepository.save(book);

		return bookId;
	}

	public Integer updateArchivedStatus(Integer bookId, Authentication currentUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

		if (!Objects.equals(book.getCreatedBy(), SecurityUtils.getUserId(currentUser))) {
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

		if (Objects.equals(book.getCreatedBy(), SecurityUtils.getUserId(currentUser))) {
			throw new OperationNotPermittedException("You are not allowed to borrow your own book.");
		}

		final boolean isAlreadyBorrowedByUser = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,
				SecurityUtils.getUserId(currentUser));
		if (isAlreadyBorrowedByUser) {
			throw new OperationNotPermittedException("Requested book is already borrowed.");
		}

		final boolean isAlreadyBorrowedByOtherUser = bookTransactionHistoryRepository.isAlreadyBorrowed(bookId);

		BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
				.userId(SecurityUtils.getUserId(currentUser))
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

		if (Objects.equals(book.getCreatedBy(), SecurityUtils.getUserId(currentUser))) {
			throw new OperationNotPermittedException("You are not allowed to borrow or return your own book.");
		}

		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
				.findByBookIdAndUserId(bookId, SecurityUtils.getUserId(currentUser))
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

		if (!Objects.equals(book.getCreatedBy(), SecurityUtils.getUserId(currentUser))) {
			throw new OperationNotPermittedException("You are not allowed to return a book that isn't your own.");
		}

		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
				.findByBookIdAndOwnerId(bookId, SecurityUtils.getUserId(currentUser))
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

		var bookCover = fileStorageService.saveFile(file, SecurityUtils.getUserId(currentUser));

		// Need to set back to String instead of byte array
		// book.setBookCover(bookCover);
		bookRepository.save(book);

	}
}

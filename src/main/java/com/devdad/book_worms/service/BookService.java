package com.devdad.book_worms.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devdad.book_worms.common.PageResponse;
import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.dto.book.BorrowedBookResponseDTO;
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
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
		List<BorrowedBookResponseDTO> bookResponse = allBorrowedBooks.stream().map(BookMapper::toBorrwedBookResponseDTO)
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
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
		List<BorrowedBookResponseDTO> bookResponse = allBorrowedBooks.stream().map(BookMapper::toBorrwedBookResponseDTO)
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
}

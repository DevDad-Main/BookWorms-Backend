package com.devdad.book_worms.mapper;

import java.util.Base64;

import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.dto.book.BorrowedBookResponseDTO;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.history.BookTransactionHistory;
import com.devdad.book_worms.respository.BookTransactionHistoryRepository;
import com.devdad.book_worms.util.FileUtils;

public class BookMapper {

	public static Book toBook(BookRequestDTO request, String userId) {
		return Book.builder()
				.id(request.id())
				.title(request.title())
				.isbn(request.isbn())
				.authorName(request.authorName())
				.synopsis(request.synopsis())
				.archived(false)
				.shareable(request.shareable())
				.createdBy(userId)
				.build();
	}

	public static BookResponseDTO toDTOResponse(Book book) {
		// Check if cover exists, otherwise default to null
		String base64Cover = null;
		if (book.getBookCover() != null && book.getBookCover().length > 0) {
			base64Cover = Base64.getEncoder().encodeToString(book.getBookCover());
		}

		return BookResponseDTO.builder()
				.id(book.getId())
				.title(book.getTitle())
				.authorName(book.getAuthorName())
				.isbn(book.getIsbn())
				.synopsis(book.getSynopsis())
				.rate(book.getBookRating())
				.archived(book.isArchived())
				.shareable(book.isShareable())
				// .owner(book.getCreatedBy())
				.cover(base64Cover)
				// NOTE: If using local file storage then use the method
				// .cover(FileUtils.readFileFromLocation(book.getBookCover()))
				.build();
	}

	public static BorrowedBookResponseDTO toBorrowedBookResponseDTO(BookTransactionHistory history) {
		return BorrowedBookResponseDTO.builder()
				.id(history.getBook().getId())
				.title(history.getBook().getTitle())
				.authorName(history.getBook().getAuthorName())
				.isbn(history.getBook().getIsbn())
				.rate(history.getBook().getBookRating())
				.returned(history.isReturned())
				.returnApproved(history.isReturnApproved())
				.build();
	}
}

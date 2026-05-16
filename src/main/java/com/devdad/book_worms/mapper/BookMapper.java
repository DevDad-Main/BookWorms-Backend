package com.devdad.book_worms.mapper;

import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.model.book.Book;

public class BookMapper {

	public static Book toBook(BookRequestDTO request){
		return  Book.builder()
			.id(request.id())
			.title(request.title())
			.authorName(request.authorName())
			.synopsis(request.synopsis())
			.archived(false)
			.shareable(request.shareable())
			.build();
	}

	public static BookResponseDTO toDTOResponse(Book book){
		return BookResponseDTO.builder()
			.id(book.getId())
			.title(book.getTitle())
			.authorName(book.getAuthorName())
			.isbn(book.getIsbn())
			.synopsis(book.getSynopsis())
			.rate(book.getBookRating())
			.archived(book.isArchived())
			.shareable(book.isShareable())
			.owner(book.getOwner().fullName())
			// .cover() TODO: implemenet image upload later
			.build();
	}
}

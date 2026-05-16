package com.devdad.book_worms.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devdad.book_worms.dto.book.BookRequestDTO;
import com.devdad.book_worms.dto.book.BookResponseDTO;
import com.devdad.book_worms.mapper.BookMapper;
import com.devdad.book_worms.model.book.Book;
import com.devdad.book_worms.model.user.User;
import com.devdad.book_worms.respository.BookRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;

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
}

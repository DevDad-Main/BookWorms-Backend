package com.devdad.book_worms.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.book.Book;

@Repository
																																			// Allows for the support of specifications in Jpa queries
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

	@Query("""
			SELECT book
			FROM Book book
			WHERE book.archived = false
			AND book.shareable = true
			AND book.createdBy != :userId
			""")
	Page<Book> findAllDisplayableBooks(Pageable pageable, @Param("userId") String userId);
}

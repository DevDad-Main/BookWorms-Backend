package com.devdad.book_worms.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.history.BookTransactionHistory;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

	@Query("""
			SELECT history
			FROM BookTransactionHistoryRepository history
			WHERE history.user.id = :userId
			""")
	Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

	@Query("""
			SELECT history
			FROM BookTransactionHistoryRepository history
			WHERE history.book.owner.id = :userId
			""")
	Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

}

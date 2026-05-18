package com.devdad.book_worms.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.book.Book;
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

	@Query("""
			SELECT
			(COUNT(*) > 0) AS isBorrowed
			FROM BookTransactionHistory bookTransactionHistory
			WHERE bookTransactionHistory.user.id = :userId
			AND bookTransactionHistory.book.id = :bookId
			AND bookTransactionHistory.returnApproved = false
			""")
	boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

	@Query("""
			SELECT transaction
			FROM BookTransactionHistory transaction
			WHERE transaction.user.id = :userId
			AND transaction.book.id = :bookId
			AND transaction.returned = false
			AND transaction.returnApproved = false
			""")
	Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer id);

	@Query("""
			SELECT transaction
			FROM BookTransactionHistory transaction
			WHERE transaction.book.owner.id = :userId
			AND transaction.book.id = :bookId
			AND transaction.returned = true
			AND transaction.returnApproved = false
			""")
	Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);

}

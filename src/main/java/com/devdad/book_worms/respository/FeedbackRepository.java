package com.devdad.book_worms.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.feedback.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

	@Query("""
				SELECT feedback
				FROM Feedback feedback
				WHERE feedback.book.id = :bookId
			""")
	Page<Feedback> findAllFeedbacksByBookId(@Param("bookId") Integer bookId, Pageable pageable);
}

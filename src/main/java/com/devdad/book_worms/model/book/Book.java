package com.devdad.book_worms.model.book;

import java.util.List;

import com.devdad.book_worms.common.BaseEntity;
import com.devdad.book_worms.model.feedback.Feedback;
import com.devdad.book_worms.model.history.BookTransactionHistory;
import com.devdad.book_worms.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private byte[] bookCover;

	// NOTE: To use local file storage uncomment this and comment out the above byte[] version
	// private String bookCover;

	private boolean archived;
	private boolean shareable;


	// @ManyToOne
	// @JoinColumn(name = "owner_id")
	// private User owner;

	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "book")
	private List<BookTransactionHistory> bookTransactionHistories;

	@Transient
	public double getBookRating(){
		if(feedbacks == null || feedbacks.isEmpty()){
			return 0.0;
		}

		var rate = this.feedbacks.stream()
			.mapToDouble(Feedback::getNote)
			.average()
			.orElse(0.0);

		double roundedrate = Math.round(rate * 10.0) / 10.0;
		return roundedrate;
	}
}

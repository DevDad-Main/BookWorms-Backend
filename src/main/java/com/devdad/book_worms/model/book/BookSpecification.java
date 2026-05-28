package com.devdad.book_worms.model.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

	public static Specification<Book> withOwnerId(String userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
	}
}

package com.devdad.book_worms.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.book.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

}

package com.devdad.book_worms.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devdad.book_worms.model.user.Token;


// @Repository
public interface TokenRepository { // extends JpaRepository<Token, Integer> {
	// Optional<Token> findByToken(String token);
}

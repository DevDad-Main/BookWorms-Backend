package com.devdad.book_worms.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDTO {

	private Integer id;
	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private String owner;
	private String cover;
	private double rate;
	private boolean archived;
	private boolean shareable;
}

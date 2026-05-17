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
public class BorrowedBookResponseDTO {

	private Integer id;
	private String title;
	private String authorName;
	private String isbn;
	private double rate;
	private boolean returned;
	private boolean returnApproved;
}

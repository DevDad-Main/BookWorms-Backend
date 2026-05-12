package com.devdad.book_worms.model.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
	ACTIVATE_ACCOUNT("activate_account"),
	WELCOME("welcome");

	private final String name;

	EmailTemplateName(String name){
		this.name = name;
	}
}

import org.springframework.http.HttpStatus;

import lombok.Getter;

public enum BusinessErrorCodes {

	NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code."),
	INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current password is incorrect."),
	NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "The new password does not match."),
	ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked.");


	@Getter
	private final int code;
	@Getter
	private final String description;
	@Getter
	private final HttpStatus httpStatus;

	BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.description = description;
	}

}

/**
 * 
 */
package com.ca.nbiapps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Balaji N
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class JSONException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public JSONException(Throwable cause) {
		super(cause);
	}
	
	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
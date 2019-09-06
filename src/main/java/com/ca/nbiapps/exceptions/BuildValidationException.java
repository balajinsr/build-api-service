/**
 * 
 */
package com.ca.nbiapps.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Balaji N
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BuildValidationException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
	private String message;
	
	//see BindingResult class 
	private List<String> details; 
	
	public BuildValidationException(Throwable cause) {
		super(cause);
	}
	
	public BuildValidationException(String message) {
		super(message);
	}
	
	public BuildValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BuildValidationException(List<String> details, Throwable cause) {
		super(cause);
		this.details=details;
	}
	 
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}
}
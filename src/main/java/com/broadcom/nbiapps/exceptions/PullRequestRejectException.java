/**
 * 
 */
package com.broadcom.nbiapps.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Balaji N
 *
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class PullRequestRejectException extends BuildException { 
	
	public PullRequestRejectException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
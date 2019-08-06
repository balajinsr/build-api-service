/**
 * 
 */
package com.broadcom.nbiapps.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Balaji N
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BuildValidationException extends BuildException { 
	private static final long serialVersionUID = 1L;
	private List<Error> errors;

	public BuildValidationException(String message) {
        super(message);
    }
	
    public BuildValidationException(String message, Throwable cause, List<Error> errors) {
        super(message, cause);
        this.errors = errors;
    }
    public BuildValidationException(String message, List<Error> errors) {
        super(message);
        this.errors = errors;
    }
    
    public BuildValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BuildValidationException(Throwable cause, List<Error> errors) {
        super(cause);
        this.errors = errors;
    }
    
	public List<Error> getErrors() {
		return errors;
	}
	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	
}
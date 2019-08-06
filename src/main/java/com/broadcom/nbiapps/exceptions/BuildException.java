/**
 * 
 */
package com.broadcom.nbiapps.exceptions;

/**
 * @author Balaji N
 *
 */
public class BuildException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String message;
	
    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }
    public BuildException(String message) {
        super(message);
    }
    public BuildException(Throwable cause) {
        super(cause);
    }
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}

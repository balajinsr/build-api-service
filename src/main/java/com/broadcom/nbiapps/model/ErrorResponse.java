/**
 * 
 */
package com.broadcom.nbiapps.model;

/**
 * @author Balaji N
 *
 */


public class ErrorResponse {
	private ErrorDetails error;

	public ErrorResponse(ErrorDetails error) {
		this.error = error;
	}
	public ErrorDetails getError() {
		return error;
	}

	public void setError(ErrorDetails error) {
		this.error = error;
	}
}

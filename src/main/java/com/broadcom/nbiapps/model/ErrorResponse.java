/**
 * 
 */
package com.broadcom.nbiapps.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Balaji N
 *
 */

@XmlRootElement(name = "errors")
public class ErrorResponse {
	private ErrorDetails error;

	public ErrorDetails getError() {
		return error;
	}

	public void setError(ErrorDetails error) {
		this.error = error;
	}
}

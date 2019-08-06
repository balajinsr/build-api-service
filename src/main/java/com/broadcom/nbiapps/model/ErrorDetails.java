/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Balaji N
 *
 */
@JsonSerialize
@JsonInclude(Include.NON_NULL)

@JsonRootName("employee")
public class ErrorDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private Date timestamp;	
	private List<Errors> errors;
	
	public ErrorDetails(int code, String message) {
        super();
        this.setCode(code);
        this.message=message;
        this.setTimestamp(new Date(System.currentTimeMillis()));
    }
	
	public ErrorDetails(String status, String message, List<Errors> errors) {
        super();
        this.setTimestamp(new Date(System.currentTimeMillis()));
        this.message = message;
        this.errors = errors;
    }
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Errors> getErrors() {
		return errors;
	}
	public void setErrors(List<Errors> errors) {
		this.errors = errors;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}

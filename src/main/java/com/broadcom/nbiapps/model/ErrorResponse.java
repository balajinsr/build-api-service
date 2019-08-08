/**
 * 
 */
package com.broadcom.nbiapps.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author Balaji N
 *
 */
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@XmlRootElement(name = "error")
public class ErrorResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	//General error message about nature of error
	private String message;
	private Date timestamp;	
	//Specific errors in API request processing
	private List<String> details;
	
	public ErrorResponse(String message, List<String> details) {
        super();
        this.message=message;
        this.setTimestamp(new Date(System.currentTimeMillis()));
    }
	
	public ErrorResponse(String status, String message, List<String> details) {
        super();
        this.setTimestamp(new Date(System.currentTimeMillis()));
        this.message = message;
        this.details = details;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}

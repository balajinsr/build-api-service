/**
 * 
 */
package com.ca.nbiapps.exception.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ca.nbiapps.exceptions.BuildValidationException;
import com.ca.nbiapps.exceptions.BuildSystemException;
import com.ca.nbiapps.exceptions.PullRequestRejectException;
import com.ca.nbiapps.model.ErrorResponse;

/**
 * @author Balaji N
 *
 */
@ControllerAdvice
public class BuildExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(BuildExceptionHandler.class);

	@ExceptionHandler(BuildValidationException.class)
	public ResponseEntity<?> buildValidationException(BuildValidationException ex, WebRequest request) {
		ErrorResponse error = new ErrorResponse("Validation Error", ex.getDetails());
		logger.error("Validation Error: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(PullRequestRejectException.class)
	public ResponseEntity<?> handlePullRequestReject(BuildValidationException ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse("Not Accepted", details);
		logger.error("Not Accepted: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}

	@ExceptionHandler({ Exception.class, RuntimeException.class, BuildSystemException.class})
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse("Server Error", details);
		logger.error("Server Error: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}

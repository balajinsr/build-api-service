/**
 * 
 */
package com.broadcom.nbiapps.exception.handler;

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

import com.broadcom.nbiapps.exceptions.BuildValidationException;
import com.broadcom.nbiapps.exceptions.JSONException;
import com.broadcom.nbiapps.exceptions.PullRequestRejectException;
import com.broadcom.nbiapps.model.ErrorResponse;

/**
 * @author Balaji N
 *
 */
@ControllerAdvice
public class BuildExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(BuildExceptionHandler.class);

	@ExceptionHandler(BuildValidationException.class)
	public ResponseEntity<?> buildValidationException(BuildValidationException ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.addAll(ex.getDetails());
		ErrorResponse error = new ErrorResponse("Validation Error", details);
		logger.error("Validation Error: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(PullRequestRejectException.class)
	public ResponseEntity<?> handlePullRequestReject(BuildValidationException ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse("Not Accepted", details);
		logger.error("Not accepted: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}

	@ExceptionHandler({ Exception.class, JSONException.class, RuntimeException.class })
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse("Server Error", details);
		logger.error("Server Error: " + ex.getMessage(), ex);
		return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}

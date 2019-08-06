/**
 * 
 */
package com.broadcom.nbiapps.exception.handler;

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
import com.broadcom.nbiapps.exceptions.PullRequestRejectException;
import com.broadcom.nbiapps.model.ErrorDetails;
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
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        logger.error("BuildValidationException: "+ex.getMessage(), ex);
        return handleExceptionInternal(ex, new ErrorResponse(errorDetails), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
	
	@ExceptionHandler(PullRequestRejectException.class)
    public ResponseEntity<?> handlePullRequestReject(BuildValidationException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        logger.error("Not accepted: "+ex.getMessage(), ex);
        return handleExceptionInternal(ex, new ErrorResponse(errorDetails), new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globleExcpetionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        logger.error("Generic BuildException : "+ex.getMessage(), ex);
        return handleExceptionInternal(ex, new ErrorResponse(errorDetails), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

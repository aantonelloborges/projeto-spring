package com.example.app.utils.exception.config;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.app.utils.exception.DefaultErrorException;


@ControllerAdvice
public class RestExceptionHandler {
	
	@ExceptionHandler(DefaultErrorException.class)
	public ResponseEntity<BadRequesExceptionsDetails> handlerBadRequestExeception(DefaultErrorException bre, HttpServletRequest request){
		BadRequesExceptionsDetails err = new BadRequesExceptionsDetails();
				err.setTimestamp(Instant.now());
				err.setStatus(HttpStatus.BAD_REQUEST.value());
				err.setError("Bad Request Exception, check the Documentation");
				err.setMessage(bre.getMessage());
				err.setPath(request.getRequestURI());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
			
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationExceptionDetails> handlerMethodArgumentNotValidException(
			MethodArgumentNotValidException exception){
		List<FieldError> fieldErros = exception.getBindingResult().getFieldErrors();
		
		String fields = fieldErros.stream().map(FieldError::getField).collect(Collectors.joining( ", "));
		String fieldsMessage = fieldErros.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining( ", "));
		
		
		ValidationExceptionDetails err = new ValidationExceptionDetails();
		err.setTimestamp(Instant.now());
		err.setStatus(HttpStatus.NOT_FOUND.value());
		err.setError("Bad Request Exception, check the Documentation");
		err.setFields(fields);
		err.setFieldsMessage(fieldsMessage);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
			
	}
	
	
	
	
}

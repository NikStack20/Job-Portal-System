package com.ncst.job.portal.globalExceptionHandler;
import java.time.LocalDateTime;  
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.ncst.job.portal.loadouts.ApiExceptions;
 

@RestControllerAdvice
public class GlobalExceptionHandel {

	@ExceptionHandler(ResourceNotFoundException.class) // We Gave Power to this class for Exception Generation
	public ResponseEntity<ApiExceptions> ResourceNotFoundExceptionHandel(ResourceNotFoundException ex) {
		String message = ex.getMessage();
		ApiExceptions apiExceptions = new ApiExceptions(message, false);
		return new ResponseEntity<ApiExceptions>(apiExceptions, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class) // We Gave Power to this class for Exception Generation
	public ResponseEntity<Map<String, String>> handelMethodArgsNotValidException(MethodArgumentNotValidException ex) {
		Map<String, String> resp = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			resp.put(fieldName, message);
		});

		return new ResponseEntity<Map<String, String>>(resp, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFound.class) // We Gave Power to this class for Exception Generation
	public ResponseEntity<ApiExceptions> NotFound(NotFound ex) {
		String message = ex.getMessage();
		ApiExceptions apiExceptions = new ApiExceptions(message, false);
		return new ResponseEntity<ApiExceptions>(apiExceptions, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadCredentialsException.class) // We Gave Power to this class for Exception Generation
	public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", HttpStatus.UNAUTHORIZED.value());
		errorBody.put("error", "Unauthorized");
		errorBody.put("message", "Invalid username or password. Please check your credentials.");
		errorBody.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
	}

	// Add more handlers if needed
}

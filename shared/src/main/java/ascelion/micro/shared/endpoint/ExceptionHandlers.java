package ascelion.micro.shared.endpoint;

import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.netflix.client.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler.
 */
@ControllerAdvice
public class ExceptionHandlers {

	static private String nameOf(Path path) {
		final String str = path.toString();
		final int dot = str.lastIndexOf('.');

		return dot < 0 ? str : str.substring(dot + 1);
	}

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	private HttpServletRequest request;

	/**
	 * Rollbacks; if the cause is a constraint violation go to 400
	 */
	@ExceptionHandler
	public ResponseEntity<?> handleException(RollbackException ex) {
		final Throwable cause = ex.getCause();

		if (cause instanceof ConstraintViolationException) {
			return handleException((ConstraintViolationException) cause);
		}

		throw ex;
	}

	@ExceptionHandler
	public ResponseEntity<?> handleException(SocketException ex) {
		return exceptionResponse(BAD_GATEWAY, ex.getMessage());
	}

	/**
	 * Constraint violations go to 400.
	 */
	@ExceptionHandler
	public ResponseEntity<?> handleException(ConstraintViolationException ex) {
		final Map<Path, String>[] messages = ex.getConstraintViolations().stream()
				.map(v -> singletonMap(nameOf(v.getPropertyPath()), v.getMessage()))
				.toArray(Map[]::new);

		return exceptionResponse(BAD_REQUEST, (Object[]) messages);
	}

	/**
	 * Invalid parameters go to 400.
	 */
	@ExceptionHandler
	public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
		final Map<String, String>[] messages = ex.getBindingResult()
				.getFieldErrors().stream()
				.map(e -> singletonMap(e.getField(), e.getDefaultMessage()))
				.toArray(Map[]::new);

		return exceptionResponse(BAD_REQUEST, (Object[]) messages);
	}

	@ExceptionHandler
	public ResponseEntity<?> handleException(EntityNotFoundException ex) {
		return exceptionResponse(NOT_FOUND, ex.getMessage());
	}

	/**
	 * Generic response status handler.
	 */
	@ExceptionHandler
	public ResponseEntity<?> handleException(ResponseStatusException ex) {
		return exceptionResponse(ex.getStatus(), ex.getReason());
	}

	ResponseEntity<?> exceptionResponse(HttpStatus status, Object... messages) {
		final Map<String, Object> values = new HashMap<>();

		values.put("source", this.appName);
		values.put("timestamp", LocalDateTime.now());
		values.put("messages", messages);
		values.put("path", this.request.getRequestURI());
		ofNullable(this.request.getQueryString())
				.ifPresent(s -> values.put("query", s));

		return ResponseEntity
				.status(status)
				.contentType(APPLICATION_JSON)
				.body(values);
	}
}

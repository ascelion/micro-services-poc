package ascelion.micro.shared.endpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import feign.FeignException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnClass(FeignException.class)
public class FeignExceptionHandler {
	@ExceptionHandler
	public ResponseEntity<?> handleException(FeignException ex) {
		return ResponseEntity.status(ex.status())
				.contentType(APPLICATION_JSON)
				.body(ex.contentUTF8());
	}
}

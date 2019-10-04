package ascelion.micro.shared.endpoint;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.netflix.client.ClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ConditionalOnClass({
		FeignException.class,
		ClientException.class, })
@RequiredArgsConstructor
public class FeignExceptionHandler {
	private final ExceptionHandlers ehs;

	@ExceptionHandler
	public ResponseEntity<?> handleException(FeignException ex) {
		return ResponseEntity.status(ex.status())
				.contentType(APPLICATION_JSON)
				.body(ex.contentUTF8());
	}

	@ExceptionHandler
	public ResponseEntity<?> handleException(ClientException ex) {
		return this.ehs.exceptionResponse(BAD_GATEWAY, ex.getErrorMessage());
	}
}

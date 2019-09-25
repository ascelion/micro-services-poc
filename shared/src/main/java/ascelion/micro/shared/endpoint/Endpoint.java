package ascelion.micro.shared.endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseBody;

@Retention(RUNTIME)
@Target(TYPE)
@Component
@ResponseBody
@Validated
public @interface Endpoint {
	String[] value();
}

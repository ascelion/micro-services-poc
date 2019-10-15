package ascelion.micro.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	@AliasFor(annotation = Component.class)
	String value() default "";
}

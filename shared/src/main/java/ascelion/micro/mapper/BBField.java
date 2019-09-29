package ascelion.micro.mapper;

import java.lang.annotation.Retention;
import java.util.function.BiFunction;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface BBField {
	String from();

	Class<?> hintFrom() default Void.class;

	String to() default "";

	Class<?> hintTo() default Void.class;

	@SuppressWarnings("rawtypes")
	Class<? extends BiFunction> converter() default BiFunction.class;

}

package ascelion.micro.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Repeatable(BBMap.Repeatable.class)
public @interface BBMap {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	@interface Repeatable {
		BBMap[] value();
	}

	Class<?> to() default Void.class;

	Class<?> from() default Void.class;

	BBField[] fields() default {};

	String[] excludes() default {};

	boolean bidi() default true;
}

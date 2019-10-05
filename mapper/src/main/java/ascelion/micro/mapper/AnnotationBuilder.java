package ascelion.micro.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.core.annotation.AnnotationUtils.synthesizeAnnotation;

import lombok.SneakyThrows;

final class AnnotationBuilder<A extends Annotation> {
	static public <A extends Annotation> AnnotationBuilder<A> create(Class<A> source) {
		return new AnnotationBuilder<>(source);
	}

	static public <A extends Annotation> AnnotationBuilder<A> create(A source) {
		return new AnnotationBuilder<>(source);
	}

	private final Map<String, Object> values = new HashMap<>();
	private final Class<A> type;

	private AnnotationBuilder(Class<A> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	private AnnotationBuilder(A source) {
		this.type = (Class<A>) source.annotationType();

		for (final Method m : this.type.getMethods()) {
			if (m.getDeclaringClass() == this.type) {
				this.values.put(m.getName(), m.invoke(source));
			}
		}
	}

	public AnnotationBuilder<A> with(String name, Object value) {
		this.values.put(name, value);

		return this;
	}

	public A build() {
		return synthesizeAnnotation(this.values, this.type, null);
	}
}

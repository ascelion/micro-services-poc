package ascelion.micro.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mappings {

	static public <T> T copyProperties(@NonNull Object source, @NonNull Supplier<T> target, boolean skipNull) {
		return copyProperties(source, target.get(), skipNull);
	}

	static public <T> T copyProperties(@NonNull Object source, @NonNull T target, boolean skipNull) {
		final Map<String, PropertyDescriptor> spm = propertiesMap(source.getClass());
		final Map<String, PropertyDescriptor> tpm = propertiesMap(target.getClass());

		tpm.values().stream()
				.filter(desc -> spm.containsKey(desc.getName()))
				.map(desc -> new ImmutablePair<>(spm.get(desc.getName()).getReadMethod(), desc.getWriteMethod()))
				.filter(pair -> pair.left != null && pair.right != null)
				.forEach(pair -> copyProperty(pair, source, target, skipNull));

		return target;
	}

	private static Map<String, PropertyDescriptor> propertiesMap(Class<?> type) {
		try {
			return stream(Introspector.getBeanInfo(type).getPropertyDescriptors())
					.collect(toMap(PropertyDescriptor::getName, identity()));
		} catch (final IntrospectionException e) {
			throw new MappingsException(type.getName(), e);
		}
	}

	private static void copyProperty(ImmutablePair<Method, Method> pair, Object source, Object target, boolean skipNull) {
		final Object value;

		try {
			value = pair.left.invoke(source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new MappingsException(pair.left.toString(), e);
		}

		if (value != null || !skipNull) {
			try {
				pair.right.invoke(target, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new MappingsException(pair.right.toString(), e);
			}
		}
	}
}

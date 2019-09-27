package ascelion.micro.shared.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.constructor.ConstructorResolverStrategy.ConstructorMapping;
import ma.glasnost.orika.constructor.SimpleConstructorResolverStrategy;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import ma.glasnost.orika.metadata.ClassMap;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BeanToBeanMapper {

	@RequiredArgsConstructor
	@EqualsAndHashCode
	static private class Key {
		final Class<?> typeA;
		final Class<?> typeB;
		final boolean mapNulls;
	}

	private final Map<Key, BoundMapperFacade<?, ?>> mappers = new ConcurrentHashMap<>();

	@Value("${orika.sources:false}")
	private boolean sources;

	public <T> T copy(@NonNull Object source, @NonNull T target, boolean mapNulls) {
		final BoundMapperFacade<Object, T> m = mapper(source.getClass(), target.getClass(), mapNulls);

		return m.map(source, target);
	}

	public <T> T copy(@NonNull Object source, @NonNull Class<T> target, boolean mapNulls) {
		final BoundMapperFacade<Object, T> m = mapper(source.getClass(), target, mapNulls);

		return m.map(source);
	}

	private <T> BoundMapperFacade<Object, T> mapper(Class<?> source, Class<?> target, boolean mapNulls) {
		final Key key = new Key(source, target, mapNulls);

		return (BoundMapperFacade<Object, T>) this.mappers
				.compute(key, (k, v) -> {
					return v != null ? v : buildMapper(key);
				});
	}

	private BoundMapperFacade<?, ?> buildMapper(Key key) {
		final Builder bld = new DefaultMapperFactory.Builder()
				.constructorResolverStrategy(this::resolve)
				.mapNulls(key.mapNulls);

		if (this.sources) {
			bld.compilerStrategy(new EclipseJdtCompilerStrategy());
		}

		return bld.build()
				.getMapperFacade(key.typeA, key.typeB, true);
	}

	public <T, A, B> ConstructorMapping<T> resolve(ClassMap<A, B> classMap, Type<T> sourceType) {
		final boolean aToB = classMap.getBType().equals(sourceType);
		final Type<?> targetClass = aToB ? classMap.getBType() : classMap.getAType();

		final Constructor<?> def = stream(targetClass.getRawType().getConstructors())
				.filter(c -> c.getParameterCount() == 0)
				.findFirst().orElse(null);

		if (def == null || !Modifier.isPublic(def.getModifiers())) {
			return new SimpleConstructorResolverStrategy().resolve(classMap, sourceType);
		}

		final ConstructorMapping<T> cm = new ConstructorMapping<>();

		cm.setConstructor((Constructor<T>) def);
		cm.setParameterTypes(new Type[0]);

		return cm;
	}
}

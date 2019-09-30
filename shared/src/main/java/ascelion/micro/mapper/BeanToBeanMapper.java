package ascelion.micro.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import ascelion.micro.shared.utils.AnnotationBuilder;

import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.constructor.ConstructorResolverStrategy.ConstructorMapping;
import ma.glasnost.orika.constructor.SimpleConstructorResolverStrategy;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import ma.glasnost.orika.metadata.ClassMap;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.FieldMapBuilder;
import ma.glasnost.orika.metadata.MapperKey;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
@Lazy
@RequiredArgsConstructor
public class BeanToBeanMapper implements InitializingBean, BeanClassLoaderAware {

	static private final Logger L = loggerForThisClass();

	@RequiredArgsConstructor
	@EqualsAndHashCode
	@ToString
	static private class Key {
		final Class<?> typeA;
		final Class<?> typeB;
		final boolean mapNulls;
	}

	private final Map<Key, MapperFacade> mappers = new ConcurrentHashMap<>();

	@Value("${orika.sources:false}")
	private boolean sources;
	private ClassLoader cld;

	public <T> T[] createArray(Class<T> target, Object[] sources) {
		return createArray(target, stream(sources));
	}

	public <T> T[] createArray(Class<T> target, Collection<?> sources) {
		return createArray(target, sources.stream());
	}

	public <T> T[] createArray(Class<T> target, Stream<?> sources) {
		return sources
				.map(source -> create(target, source))
				.toArray(n -> (T[]) Array.newInstance(target, n));
	}

	public <T> List<T> createList(Class<T> target, Object[] sources) {
		return createList(target, stream(sources));
	}

	public <T> List<T> createList(Class<T> target, Collection<?> sources) {
		return createList(target, sources.stream());
	}

	public <T> List<T> createList(Class<T> target, Stream<?> sources) {
		return sources
				.map(source -> create(target, source))
				.collect(toList());
	}

	public <T> T create(@NonNull Class<T> target, @NonNull Object... sources) {
		T instance = null;

		for (final Object source : sources) {
			final MapperFacade m = mapper(source.getClass(), target, true);

			if (instance == null) {
				instance = m.map(source, target);
			} else {
				m.map(source, instance);
			}
		}
		return instance;
	}

	public <T> T copyWithNulls(@NonNull T target, @NonNull Object... sources) {
		return copy(true, target, sources);
	}

	public <T> T copyWithoutNulls(@NonNull T target, @NonNull Object... sources) {
		return copy(false, target, sources);
	}

	private <T> T copy(boolean mapNulls, @NonNull T target, @NonNull Object... sources) {
		for (final Object source : sources) {
			final MapperFacade m = mapper(source.getClass(), target.getClass(), mapNulls);

			m.map(source, target);
		}

		return target;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final ClassPathScanningCandidateComponentProvider cps = new ClassPathScanningCandidateComponentProvider(false);

		cps.addIncludeFilter(new AnnotationTypeFilter(BBMap.class));
		cps.findCandidateComponents("ascelion")
				.forEach(this::initMapperFromBean);
	}

	@Override
	public void setBeanClassLoader(ClassLoader cld) {
		this.cld = cld;
	}

	@SneakyThrows
	private void initMapperFromBean(BeanDefinition def) {
		final Class<?> type = this.cld.loadClass(def.getBeanClassName());

		if (AnnotationUtils.findAnnotation(type, Component.class) != null) {
			initMapperFromComponent(type);
		} else {
			initMapperFromType(type);
		}
	}

	private void initMapperFromComponent(Class<?> type) {
		for (final BBMap map : type.getAnnotationsByType(BBMap.class)) {
			final Class<?> typeA = map.from();
			final Class<?> typeB = map.to();

			if (typeA == Void.class || typeB == Void.class) {
				throw new RuntimeException("Both @BBMap.from() and @BBMap.to() must be specified at component " + type.getName());
			}
			if (typeA == typeB) {
				throw new RuntimeException("Cannot use the same type for @BBMap.from() and @BBMap.to() at component " + type.getName());
			}

			initMapper(new Key(typeA, typeB, true), map, type);
			initMapper(new Key(typeA, typeB, false), map, type);
		}
	}

	private void initMapperFromType(Class<?> type) {
		for (BBMap map : type.getAnnotationsByType(BBMap.class)) {
			final Class<?> typeA = map.from();
			final Class<?> typeB = map.to();

			if (typeA != Void.class && typeB != Void.class) {
				throw new RuntimeException("Only one of @BBMap.from() or @BBMap.to() must be specified at type " + type.getName());
			}
			if (typeA == typeB) {
				throw new RuntimeException("Cannot use the same type for @BBMap.from() and @BBMap.to() at component " + type.getName());
			}

			final AnnotationBuilder<BBMap> bld = AnnotationBuilder.create(map);

			if (typeA == type) {
				throw new RuntimeException("@BBMap.from() is the same as the declaring type " + type.getName());
			} else {
				bld.with("from", typeA == Void.class ? type : typeA);
			}
			if (typeB == type) {
				throw new RuntimeException("@BBMap.to() is the same as the declaring type " + type.getName());
			} else {
				bld.with("to", typeB == Void.class ? type : typeB);
			}

			map = bld.build();

			initMapper(new Key(map.from(), map.to(), true), map, type);
			initMapper(new Key(map.from(), map.to(), false), map, type);
		}

		final BBMap map = AnnotationBuilder.create(BBMap.class)
				.with("from", type)
				.with("to", type)
				.build();

		initMapper(new Key(type, type, true), map, type);
		initMapper(new Key(type, type, false), map, type);
	}

	//
	private void initMapper(Key key, BBMap map, Class<?> source) {
		initOneWayMapper(key, map, source);

		if (key.typeA != key.typeB && map.bidi()) {
			final AnnotationBuilder<BBMap> bld = AnnotationBuilder.create(BBMap.class)
					.with("from", key.typeB)
					.with("to", key.typeA)
					.with("excludes", map.excludes());

			final BBField[] fields = stream(map.fields())
					.map(f -> {
						return AnnotationBuilder.create(BBField.class)
								.with("from", f.to().isEmpty() ? f.from() : f.to())
								.with("to", f.from())
								.with("hintFrom", f.hintTo())
								.with("hintTo", f.hintFrom())
								.build();
					})
					.toArray(BBField[]::new);
//
			bld.with("fields", fields);

			map = bld.build();

			key = new Key(map.from(), map.to(), key.mapNulls);

			initOneWayMapper(key, map, source);
		}
	}

	private void initOneWayMapper(Key key, BBMap map, Class<?> source) {
		if (this.mappers.containsKey(key)) {
			throw new RuntimeException("Already found " + key);
		}

		this.mappers.put(key, createOneWayMapper(key, map, source));
	}

	private MapperFacade createOneWayMapper(Key key, BBMap map, Class<?> source) {
		final Type<?> typeA = TypeFactory.valueOf(key.typeA);
		final Type<?> typeB = TypeFactory.valueOf(key.typeB);

		L.trace("{}: adding mapping {} nulls from {} to {}", source.getName(), key.mapNulls ? "with" : "without", key.typeA.getName(), key.typeB.getName());

		final MapperFactory mf = buildFactory(key.mapNulls);
		final ClassMapBuilder<?, ?> cmb = mf.classMap(typeA, typeB);

		for (final BBField f : map.fields()) {
			String to = f.to();

			if (to.isEmpty()) {
				to = f.from();
			}

			final FieldMapBuilder<?, ?> fmb = cmb.fieldMap(f.from(), to);

			L.trace("\tincluded from {} to {}", f.from(), to);

			if (f.hintFrom() != Void.class) {
				fmb.aElementType(f.hintFrom());
			}
			if (f.hintTo() != Void.class) {
				fmb.bElementType(f.hintTo());
			}

			fmb.add();
		}

		for (final String x : map.excludes()) {
			L.trace("\texcluded {}", x);

			cmb.exclude(x);
		}

		cmb.byDefault().register();

		if (L.isDebugEnabled()) {
			final MapperKey k = new MapperKey(typeA, typeB);

			L.debug("ClassMap {}", k);

			mf.getClassMap(k).getFieldsMapping().forEach(fm -> {
				L.debug("\t{} {} {}", fm, fm.isExcluded() ? "excluded" : "", fm.isByDefault() ? "default" : "");
			});
		}

		return mf.getMapperFacade();
	}

	private <T> MapperFacade mapper(Class<?> source, Class<?> target, boolean mapNulls) {
		final Key key = new Key(source, target, mapNulls);

		return this.mappers.compute(key, (k, v) -> {
			if (v != null) {
				return v;
			}

			final BBMap map = AnnotationBuilder.create(BBMap.class)
					.with("from", source)
					.with("to", target)
					.build();

			return createOneWayMapper(key, map, source);
		});
	}

	private MapperFactory buildFactory(boolean mapNulls) {
		final DefaultMapperFactory.Builder bld = new DefaultMapperFactory.Builder()
				.constructorResolverStrategy(this::resolve)
				.mapNulls(mapNulls);

		if (this.sources) {
			bld.compilerStrategy(new EclipseJdtCompilerStrategy());
		}

		return bld.build();
	}

	private <T, A, B> ConstructorMapping<T> resolve(ClassMap<A, B> classMap, Type<T> sourceType) {
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

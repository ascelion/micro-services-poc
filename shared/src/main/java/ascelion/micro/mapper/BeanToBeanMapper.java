package ascelion.micro.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static ascelion.micro.shared.utils.LogUtils.loggerForThisClass;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.newSetFromMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.annotation.AnnotationUtils.synthesizeAnnotation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@RequiredArgsConstructor
public class BeanToBeanMapper implements InitializingBean {

	static private final Logger L = loggerForThisClass();

	@RequiredArgsConstructor
	@EqualsAndHashCode
	@ToString
	static private class Key {
		final Class<?> typeA;
		final Class<?> typeB;
		final boolean mapNulls;
	}

	private final Set<Class<?>> components = newSetFromMap(new IdentityHashMap<>());
	private final Map<Key, MapperFacade> mappers = new HashMap<>();
	private final ApplicationContext acx;

	@Value("${orika.sources:false}")
	private boolean sources;

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

	@Override
	public void afterPropertiesSet() throws Exception {
		for (final Object confs : this.acx.getBeansWithAnnotation(BBMap.class).values()) {
			for (final BBMap a : confs.getClass().getAnnotationsByType(BBMap.class)) {
				initMapperFromComponent(a, confs.getClass(), true);
				initMapperFromComponent(a, confs.getClass(), false);
			}
		}
	}

	private <T> T copy(boolean mapNulls, @NonNull T target, @NonNull Object... sources) {
		for (final Object source : sources) {
			final MapperFacade m = mapper(source.getClass(), target.getClass(), mapNulls);

			m.map(source, target);
		}

		return target;
	}

	private void initMapperFromComponent(BBMap a, Class<?> type, boolean mapNulls) {
		if (a.to() == Void.class || a.from() == Void.class) {
			throw new RuntimeException(format("%s: at least one of the @BBMap.to() and @BBMap.from() must be filled in", type));
		}

		initMapper(a, mapNulls, type);
	}

	private void initMapperFromType(BBMap a, Class<?> type, boolean mapNulls) {
		final Map<String, Object> map = new HashMap<>();

		if (a.to() != Void.class && a.from() != Void.class) {
			throw new RuntimeException(format("%s: only one of the @BBMap.to() and @BBMap.from() must be filled in", type));
		}

		final Class<?> from = a.from() == Void.class ? type : a.from();
		final Class<?> to = a.to() == Void.class ? type : a.to();

		map.put("from", from);
		map.put("to", to);
		map.put("fields", a.fields());
		map.put("excludes", a.excludes());
		map.put("reversed", a.bidi());

		a = synthesizeAnnotation(map, BBMap.class, null);

		initMapper(a, mapNulls, type);
	}

	private void initMapper(BBMap a, boolean mapNulls, Class<?> component) {
		if (this.components.add(component)) {
			doInitMapper(a, mapNulls, component);
		}
	}

	private void doInitMapper(BBMap a, boolean mapNulls, Class<?> component) {
		initOneWayMapper(a, mapNulls, component);

		if (a.from() != a.to() && a.bidi()) {
			final Map<String, Object> map = new HashMap<>();

			map.put("from", a.to());
			map.put("to", a.from());

			final BBField[] fields = stream(a.fields())
					.map(f -> {
						final Map<String, Object> m = new HashMap<>();

						m.put("from", f.to().isEmpty() ? f.from() : f.to());
						m.put("to", f.from());
						m.put("hintFrom", f.hintTo());
						m.put("hintTo", f.hintFrom());

						return synthesizeAnnotation(m, BBField.class, null);
					})
					.toArray(BBField[]::new);

			map.put("fields", fields);
			map.put("excludes", a.excludes());

			a = synthesizeAnnotation(map, BBMap.class, null);

			initOneWayMapper(a, mapNulls, component);
		}
	}

	private void initOneWayMapper(BBMap a, boolean mapNulls, Class<?> component) {
		final Key key = new Key(a.from(), a.to(), mapNulls);

		if (this.mappers.containsKey(key)) {
			throw new RuntimeException();
		}

		final Type<?> typeA = TypeFactory.valueOf(a.from());
		final Type<?> typeB = TypeFactory.valueOf(a.to());

		L.trace("{}: adding mapping {} nulls from {} to {}", component.getName(), mapNulls ? "with" : "without", a.from().getName(), a.to().getName());

		final MapperFactory mf = buildFactory(true);
		final ClassMapBuilder<?, ?> cmb = mf.classMap(typeA, typeB);

		for (final BBField f : a.fields()) {
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

		for (final String x : a.excludes()) {
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

		final MapperFacade bmf = mf.getMapperFacade();

		this.mappers.put(key, bmf);
	}

	private <T> MapperFacade mapper(Class<?> source, Class<?> target, boolean mapNulls) {
		final Key key = new Key(source, target, mapNulls);
		MapperFacade bmf = this.mappers.get(key);

		if (bmf != null) {
			return bmf;
		}

		synchronized (this.mappers) {
			bmf = this.mappers.get(key);

			if (bmf == null) {
				initMapper(key);
			}

			return this.mappers.get(key);
		}
	}

	private void initMapper(Key key) {
		for (final BBMap a : key.typeA.getAnnotationsByType(BBMap.class)) {
			initMapperFromType(a, key.typeA, true);
			initMapperFromType(a, key.typeA, false);
		}
		if (key.typeA != key.typeB) {
			for (final BBMap a : key.typeB.getAnnotationsByType(BBMap.class)) {
				initMapperFromType(a, key.typeB, true);
				initMapperFromType(a, key.typeB, false);
			}
		}

		final MapperFacade bmf = this.mappers.get(key);

		if (bmf == null) {
			this.mappers.put(key, buildMapper(key));
		}
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

	private MapperFacade buildMapper(Key key) {
		return buildFactory(key.mapNulls).getMapperFacade();
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

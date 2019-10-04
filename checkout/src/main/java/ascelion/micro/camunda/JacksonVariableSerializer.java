package ascelion.micro.camunda;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractTypedValueSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.impl.value.UntypedValueImpl;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JacksonVariableSerializer extends AbstractTypedValueSerializer<ObjectValue> {

	private final ObjectMapper om;
	private final TypeFactory tf = TypeFactory.defaultInstance();

	public JacksonVariableSerializer(ObjectMapper om) {
		super(ValueType.OBJECT);

		this.om = om;
	}

	@Autowired
	public void configure(SpringProcessEngineConfiguration cf) {
		cf
				.setDefaultSerializationFormat(APPLICATION_JSON_VALUE)
				.getVariableSerializers()
				.addSerializer(this, 0);
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String getSerializationDataformat() {
		return APPLICATION_JSON_VALUE;
	}

	@Override
	public ObjectValue convertToTypedValue(UntypedValueImpl untypedValue) {
		return Variables.objectValue(untypedValue.getValue(), untypedValue.isTransient()).create();
	}

	@Override
	@SneakyThrows
	public void writeValue(ObjectValue value, ValueFields fields) {
		final Object v = value.getValue();

		if (v != null) {
			final JavaType j = constructType(v);
			final ObjectWriter w = this.om.writerFor(j);

			fields.setTextValue(j.toCanonical());
			fields.setTextValue2(w.writeValueAsString(v));
		} else {
			fields.setTextValue(null);
			fields.setTextValue2(null);
		}
	}

	private JavaType constructType(Object v) {
		final Class<?> t = v.getClass();

		if (t.isArray()) {
			return this.tf.constructArrayType(t.getComponentType());
		}
		if (Map.class.isAssignableFrom(t)) {
			final Map<?, ?> m = (Map) v;
			final int z = m.size();

			if (z != 0) {
				final Map.Entry<?, ?> e = m.entrySet().iterator().next();

				if (SortedMap.class.isAssignableFrom(t)) {
					return this.tf.constructMapType(SortedMap.class, constructType(e.getKey()), constructType(e.getValue()));
				}
				if (LinkedHashMap.class.isAssignableFrom(t)) {
					return this.tf.constructMapType(LinkedHashMap.class, constructType(e.getKey()), constructType(e.getValue()));
				}

				return this.tf.constructMapType(Map.class, constructType(e.getKey()), constructType(e.getValue()));
			}
		}
		if (Collection.class.isAssignableFrom(t)) {
			final Collection<?> c = (Collection) v;
			final int z = c.size();

			if (z != 0) {
				final Object o = c.iterator().next();

				return this.tf.constructCollectionLikeType(t, constructType(o));
			}
		}

		return this.tf.constructType(t);
	}

	@Override
	@SneakyThrows
	public ObjectValue readValue(ValueFields fields, boolean deserializeValue) {
		final String t = fields.getTextValue();

		if (t == null) {
			return Variables.objectValue(null).create();
		}

		final JavaType j = TypeFactory.defaultInstance()
				.constructFromCanonical(t);

		return Variables.objectValue(this.om.readValue(fields.getTextValue2(), j)).create();
	}

	@Override
	protected boolean canWriteValue(TypedValue value) {
		return true;
	}
}

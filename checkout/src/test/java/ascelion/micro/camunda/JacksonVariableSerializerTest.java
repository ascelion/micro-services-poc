package ascelion.micro.camunda;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFieldsImpl;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Test;

public class JacksonVariableSerializerTest {

	private final JacksonVariableSerializer jvs = new JacksonVariableSerializer(new ObjectMapper());
	private final ValueFields fields = new ValueFieldsImpl();

	@Test
	public void simple() {
		check(randomUUID());
	}

	@Test
	public void array() {
		final UUID[] v = { randomUUID(), randomUUID() };

		check(v);
	}

	@Test
	public void singleton_map() {
		check(singletonMap("name", "value"));
	}

	@Test
	public void tree_map() {
		final var v = new TreeMap<>();

		v.put("name", "value");

		check(v);
	}

	@Test
	public void hash_map() {
		final var v = new HashMap<>();

		v.put("name", "value");

		check(v);
	}

	@Test
	public void linked_hash_map() {
		final var v = new LinkedHashMap<>();

		v.put("name", "value");

		check(v);
	}

	private void check(final Object o1) {
		this.jvs.writeValue(Variables.objectValue(o1).create(), this.fields);

		System.out.printf("TEXT 1: %s\n", this.fields.getTextValue());
		System.out.printf("TEXT 2: %s\n\n", this.fields.getTextValue2());

		final var o2 = this.jvs.readValue(this.fields, true).getValue();

		assertThat(o1, equalTo(o2));
	}

}

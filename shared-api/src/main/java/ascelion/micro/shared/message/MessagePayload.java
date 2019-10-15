package ascelion.micro.shared.message;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessagePayload<T> {
	static public <T> MessagePayload<T> of(Optional<T> value) {
		return new MessagePayload<>(value);
	}

	static public <T> MessagePayload<T> of(T value) {
		return new MessagePayload<>(Optional.ofNullable(value));
	}

	static public <T> MessagePayload<T> empty() {
		return new MessagePayload<>(Optional.empty());
	}

	@JsonProperty
	private final Optional<T> value;
	@JsonProperty
	private final Map<String, Object> properties = new TreeMap<>();

	public MessagePayload<T> withProperty(String name, Object value) {
		this.properties.put(name, value);

		return this;
	}

	public T get() {
		return this.value.get();
	}

	public boolean isPresent() {
		return this.value.isPresent();
	}

	public void ifPresent(Consumer<? super T> consumer) {
		this.value.ifPresent(consumer);
	}

	public Optional<T> filter(Predicate<? super T> predicate) {
		return this.value.filter(predicate);
	}

	public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		return this.value.map(mapper);
	}

	public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
		return this.value.flatMap(mapper);
	}

	public T orElse(T other) {
		return this.value.orElse(other);
	}

	public T orElseGet(Supplier<? extends T> other) {
		return this.value.orElseGet(other);
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		return this.value.orElseThrow(exceptionSupplier);
	}

	@Override
	public String toString() {
		return this.value.isPresent()
				? format("MessagePayload[%s]", this.value.get())
				: "MessagePayload.empty";
	}
}

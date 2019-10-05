package ascelion.micro.shared.message;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@RequiredArgsConstructor
public abstract class MessageSenderAdapter<T> {
	static public final String HEADER_SOURCE = "source";
	static public final String HEADER_KIND = "kind";
	static public final String HEADER_CORRELATION = "correlation";

	public enum Direction {
		REQUEST, RESPONSE,
	}

	private final MessageChannel output;

	@Value("${spring.application.name}")
	private String appName;

	public final void send(Direction d, UUID pid, String kind, MessagePayload<T> payload) {
		send(this.appName, d, pid, kind, payload);
	}

	@SneakyThrows
	public void send(String source, Direction d, UUID pid, String kind, MessagePayload<T> payload) {
		final var message = MessageBuilder
				.withPayload(payload)
				.setHeader(HEADER_CORRELATION, pid)
				.setHeader(HEADER_KIND, kind + "_" + d.name())
				.setHeader(HEADER_SOURCE, source)
				.build();

		this.output.send(message);
	}
}

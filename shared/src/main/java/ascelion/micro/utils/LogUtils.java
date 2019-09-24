package ascelion.micro.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtils {

	static public Logger loggerForThisClass() {
		final StackTraceElement[] elements = new Throwable().getStackTrace();

		if (elements.length < 2) {
			return LoggerFactory.getLogger("UNKNOWN");
		}

		return LoggerFactory.getLogger(elements[1].getClassName());
	}
}

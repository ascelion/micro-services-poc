package ascelion.micro.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtils {

	static public int randomInt(int min, int max) {
		return org.apache.commons.lang3.RandomUtils.nextInt(min, max);
	}

	static public String randomAscii(int min, int max) {
		return org.apache.commons.lang3.RandomStringUtils.randomAscii(min, max);
	}

	static public BigDecimal randomDecimal(double min, double max) {
		final double v = org.apache.commons.lang3.RandomUtils.nextDouble(min, max);

		return new BigDecimal(v)
				.setScale(2, RoundingMode.HALF_UP);
	}
}

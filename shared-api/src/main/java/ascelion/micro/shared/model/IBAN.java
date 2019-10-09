package ascelion.micro.shared.model;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(doNotUseGetters = true, of = "value")
@ToString(of = "value")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IBAN {

	@JsonCreator
	public static IBAN valueOf(String value) {
		return value != null ? new IBAN(value.trim()) : null;
	}

	@Getter(onMethod_ = @JsonValue)
	@Size(min = 12, max = 20)
	private final String value;
}

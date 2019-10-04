package ascelion.micro.account;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class IBANConverter implements AttributeConverter<IBAN, String> {

	@Override
	public String convertToDatabaseColumn(IBAN attribute) {
		return attribute != null ? attribute.getValue() : null;
	}

	@Override
	public IBAN convertToEntityAttribute(String data) {
		return IBAN.valueOf(data);
	}

}

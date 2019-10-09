package ascelion.micro.shared.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import ascelion.micro.shared.model.IBAN;

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

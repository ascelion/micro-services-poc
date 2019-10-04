package ascelion.micro.customer.api;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CardNumberConverter implements AttributeConverter<CardNumber, String> {

	@Override
	public String convertToDatabaseColumn(CardNumber attribute) {
		return attribute != null ? attribute.getValue() : null;
	}

	@Override
	public CardNumber convertToEntityAttribute(String data) {
		return CardNumber.valueOf(data);
	}

}

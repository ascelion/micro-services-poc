package ascelion.micro.shared.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import ascelion.micro.shared.model.CardNumber;

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

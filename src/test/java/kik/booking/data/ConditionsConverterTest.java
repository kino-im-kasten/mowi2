package kik.booking.data;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ConditionsConverterTest {
	@Test
	void uConditionsConverterConvertionP(){
		ConditionsConverter converter = new ConditionsConverter();

		Conditions conditions = new Conditions(25.0f, 5.0f, 1.0f, 2.0f,
			3.0f, 4.0f);

		String conditionsString = converter.convertToDatabaseColumn(conditions);

		assertEquals(conditions, converter.convertToEntityAttribute(conditionsString));
	}
}

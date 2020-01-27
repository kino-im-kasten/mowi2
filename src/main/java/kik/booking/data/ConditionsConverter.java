package kik.booking.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * An implementation of {@link AttributeConverter} to convert a {@link Conditions} to a String to save it as attribute
 * in a {@link Booking}.
 *
 * @author Erik Schönherr
 */
@Converter
public class ConditionsConverter implements AttributeConverter<Conditions, String> {
	private final String separator = ", ";

	/**
	 * Converts a {@link Conditions} object to a String so it can be saved in a database.
	 *
	 * @param conditions {@link Conditions} object that is supposed to be converted
	 * @return String representing the given {@link Conditions} object
	 */
	@Override
	public String convertToDatabaseColumn(Conditions conditions) {
		if(conditions == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(conditions.getMinimumGuarantee());
		sb.append(separator);
		sb.append(conditions.getPercentage());
		sb.append(separator);
		sb.append(conditions.getFreight());
		sb.append(separator);
		sb.append(conditions.getAdvertisement());
		sb.append(separator);
		sb.append(conditions.getSpio());
		sb.append(separator);
		sb.append(conditions.getOther());

		return sb.toString();
	}

	/**
	 * Converts a String representing a {@link Conditions} object to that object.
	 * Used to load {@link Conditions} objects from a database.
	 *
	 * @param s String representing a {@link Conditions} object
	 * @return The converted {@link Conditions} object
	 */
	@Override
	public Conditions convertToEntityAttribute(String s) {
		if(s == null || s.isBlank()) {
			return null;
		}

		String[] pieces = s.split(separator);

		//6 is the number of conditions
		if(pieces.length != 6) {
			return null;
		}

		Conditions conditions = new Conditions();

		conditions.setMinimumGuarantee(Float.parseFloat(pieces[0]));
		conditions.setPercentage(Float.parseFloat(pieces[1]));
		conditions.setFreight(Float.parseFloat(pieces[2]));
		conditions.setAdvertisement(Float.parseFloat(pieces[3]));
		conditions.setSpio(Float.parseFloat(pieces[4]));
		conditions.setOther(Float.parseFloat(pieces[5]));

		return conditions;
	}

}

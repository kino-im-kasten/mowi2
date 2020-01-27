package kik.event.management.specialEvent;

import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A validator class to validate {@link SpecialEvent}s
 * and {@link SpecialEventForm}s
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Service
public class SpecialEventValidation implements Validator {
	@Override
	public boolean supports(Class<?> aClass) {
		throw new UnsupportedOperationException("This method is not supported!");
	}
	
	/**
	 * Distributor method for various checks
	 *
	 * @param object to be checked
	 * @param errors to propagate errors throughout methods
	 */
	@Override
	public void validate(@NonNull Object object, @NonNull Errors errors) {
		if (object instanceof SpecialEventForm) {
			validateSpecialEventForm((SpecialEventForm) object, errors);
		}
	}
	
	/**
	 * Validates the given {@link SpecialEventForm} against check-parameters,
	 * such as a check for the chosen date to not be in the past
	 *
	 * @param specialEventForm user-input to be validated
	 * @param errors           to propagate errors throughout methods
	 */
	private void validateSpecialEventForm(SpecialEventForm specialEventForm, Errors errors) {
		if (errors == null) {
			return;
		}
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime chosenTime = LocalTime.parse(specialEventForm.getStart(),
		DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetTime chosenEndTime = LocalTime.parse(specialEventForm.getExpectedEnd(),
				DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		if (chosenTime.isAfter(chosenEndTime)) {
			errors.rejectValue("expectedEnd", "Expected end is before start");
		}
	}
}
